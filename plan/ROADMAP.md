# nanoMarket: план модернизации и изучения технологий

## Context

nanoMarket — учебный интернет-магазин на Spring Cloud (Java 17, Boot 3.4.0, Spring Cloud 2024.0, Maven multi-module):
ConfigServer, EurekaServer, GatewayServer, ProductService (Postgres + MinIO), CartService (Redis), OrderService (Postgres).
Цель: поднять скилл и усилить резюме. Нужно освежить проект и поэтапно внедрить Kafka, колоночную БД, полнотекстовый поиск,
observability (логи, метрики, трейсы), Keycloak, Outbox + Debezium, Schema Registry + Avro и Kubernetes.
Каждая технология должна закрывать реальную задачу магазина, а не стоять для галочки.

Выбранный стек: **Java 25 + Spring Boot 4.x**, **OpenTelemetry + Grafana LGTM**, **Elasticsearch**, **ClickHouse**,
**Kafka (KRaft) + Schema Registry/Avro + Debezium**, **Keycloak**, **k8s (k3d/kind) + Helm**.

---

## Что нашёл в проекте (это вход для Этапа 0)

**Сборка и инфраструктура**
- Корневой `pom.xml` импортирует Boot BOM вместо `<parent>`. Spring Cloud BOM продублирован в каждом модуле, версии lombok, mapstruct, liquibase и springdoc захардкожены по модулям. `liquibase-maven-plugin` подключён как dependency, есть лишние milestone-репозитории.
- В корневом pom стоит `spring-boot-starter-web` для **всех** модулей, включая реактивный GatewayServer: MVC конфликтует с Spring Cloud Gateway.
- `com.spotify:dockerfile-maven-plugin` архивирован и пушит образ на `install`. В каждом модуле лежит свой `mvnw`.
- Dockerfile: `openjdk:17.0.1-jdk-slim` (образ deprecated), внутри полный JDK, процесс от root, jar собирается вне Docker.
- `docker-compose.yml`: у MinIO том `./minio_data:/data`, а сервер запущен с `/minio-image/storage`, поэтому данные не сохраняются. `REDIS_PASSWORD` не применяется. Product и Order делят одну БД `nanomarket_db` (shared database anti-pattern).
- Конфиги живут во внешнем git-репо `nanomarket-config-repo`, а частичные копии в `ConfigServer/src/main/resources/config/*.yml` расходятся с ним (там `localhost`). Секреты лежат в открытом виде.
- Тестов фактически нет: только `contextLoads`.

**Баги в бизнес-логике**
- `OrderService/.../service/impl/ProductReservationServiceImpl.java`:
  - `@Bulkhead`, `@CircuitBreaker` и `@Retry` висят на **private** методах `reserveProducts` и `getProductAvailability`. Spring AOP через прокси их не видит, аннотации не срабатывают.
  - `fallbackCheckStockAndReserveProducts` возвращает пустой `OrderEntity`, `OrderController.createOrder` его сохраняет. В итоге клиент получает 200 и пустой заказ с total=0.
  - `@Retry` на всём `checkStockAndReserveProducts` неидемпотентен: повтор после успешного reserve зарезервирует товар второй раз. Компенсации тоже нет: если упадут `clearCart` или `save`, остатки уже списаны, а заказа нет. Это решается сагой на Этапе 2.
- `ProductService/.../service/impl/ProductServiceImpl.reserveProducts`: read-modify-write без блокировок, при параллельных заказах возможен overselling.
- `ProductController.getProducts`: `sort` парсится вручную через `split(",")`. Значение без запятой даёт 500, имя поля не валидируется.
- `Product.equals/hashCode` берут `getId()`, у нового объекта это NPE.
- Аутентификации нет: `customerId` и `cartId` приходят от клиента, `CartController` отдаёт entity.
- В `OrderServiceImpl.updateOrderStatus` стоит `//TODO: add logic for statuses processing(message sending?)`. Это естественная точка входа для Kafka.
- `ProductSpecification` ищет через `LIKE %name%`. Это естественная точка входа для Elasticsearch.

---

## Целевая архитектура

```
                 Keycloak (OIDC)
                      │ JWT
Client ──► Gateway (SCG, OAuth2 RS, Redis rate-limit) ──► product / cart / order / search / analytics
                                                            │
  product-service ─(outbox→Debezium)─► product.events ───► search-service ──► Elasticsearch
  order-service  ─(outbox→Debezium)─► order.events ──┬──► product-service (резерв/компенсация) ─► inventory.events ─► order-service
                                                     ├──► cart-service (очистка корзины)
                                                     ├──► notification-service (Mailpit)
                                                     └──► ClickHouse (Kafka Engine + MV) ◄── analytics-service (отчёты)
  Все сервисы ─OTLP─► OTel Collector ─► Tempo (трейсы) / Prometheus (метрики) / Loki (логи) ─► Grafana
  Kafka (KRaft) + Schema Registry (Avro) + Kafka Connect (Debezium)
```

Новые сервисы: **search-service**, **analytics-service**, **notification-service**, плюс модуль **events-contracts** (Avro-схемы).
Eureka и ConfigServer остаются для docker-compose. В k8s их отключаем профилем: там используются Service DNS и ConfigMaps.

---

## Этап 0. Подготовка и модернизация (≈2–3 недели)

Каждый шаг делаем отдельной веткой и отдельным PR, после каждого шага проект собирается и проходит тесты.

**0.1 Чистка сборки.** Корневой `pom.xml`:
- `spring-boot-starter-parent` ставим как `<parent>`, Spring Cloud BOM подключаем один раз в root `dependencyManagement`, версии библиотек выносим в `<properties>`.
- Убираем `starter-web` из root, spotify-плагин, milestone-репо, `liquibase-maven-plugin` из dependencies. Оставляем один `mvnw` в корне.
- Добавляем `maven-enforcer-plugin` (версия Java, dependency convergence).

**0.2 Java 25.** `java.version=25`. Lombok берём версии с поддержкой JDK 25, проверяем MapStruct и связку lombok-mapstruct-binding.

**0.3 Spring Boot 3.4 → 3.5 → 4.x** (+ совместимый Spring Cloud 2025.1.x; точные версии сверяем на start.spring.io).
- Миграцию автоматизируем рецептами **OpenRewrite** (`rewrite-spring`: UpgradeSpringBoot_3_5, затем 4_0). Это ещё и отдельный навык для резюме.
- Что ломается в Boot 4: модульные стартеры, Jackson 3 (`tools.jackson`), Hibernate 7, переименованные артефакты Gateway (`spring-cloud-starter-gateway-server-webflux`), springdoc под Boot 4.
- **OpenFeign → HTTP Service Clients** (`@HttpExchange` + `RestClient`). Feign в maintenance mode. Затрагивает `OrderService/.../CartFeignClient.java`, `ProductFeignClient.java` и `CartService/.../ProductFeignClient.java`.
- Resilience4j: оставляем через spring-cloud-circuitbreaker или переходим на встроенные `@Retryable` / `@ConcurrencyLimit` из Spring Framework 7.

**0.4 Современная Java и Spring.**
- Включаем virtual threads (`spring.threads.virtual.enabled=true`).
- DTO переводим на records.
- Ошибки отдаём как `ProblemDetail` (RFC 9457) через единый `@RestControllerAdvice`. Доработать `GlobalExceptionHandler` в Product и Order.

**0.5 Минимальные фиксы багов** (полноценное решение придёт с сагой):
- Атомарный `UPDATE product SET quantity = quantity - :q WHERE id = :id AND quantity >= :q` или `@Version`.
- Fallback больше не сохраняет пустой заказ.
- Resilience-аннотации переносим на public-методы отдельного бина.
- Валидируем `sort` по whitelist полей.
- Чиним `equals/hashCode`.

**0.6 Конфигурация.**
- Переносим конфиги в монорепо (`config-repo/`, native-профиль ConfigServer). Проект становится самодостаточным.
- Секреты выносим в `.env` (в git не коммитим, рядом лежит `.env.example`).
- Gateway-маршруты описываем явно.

**0.7 Database per service.** Отдельные `product_db` и `order_db` с отдельными пользователями, init-скрипт в compose. Liquibase остаётся в каждом сервисе.

**0.8 Контейнеры.**
- Образы собираем через `spring-boot:build-image` (buildpacks) или multi-stage Dockerfile на `eclipse-temurin:25-jre`: non-root, layered jar, опционально AOT cache JDK 25 для быстрого старта.
- Compose:
  - разбиваем на профили `infra` / `apps` / `observability`;
  - чиним MinIO и Redis;
  - healthchecks переводим на `/actuator/health/readiness`;
  - запускаем через `docker compose` v2.

**0.9 Тесты.**
- Testcontainers + `@ServiceConnection` (Postgres, Redis), WireMock для HTTP-клиентов.
- Unit-тесты сервисов.
- Интеграционный тест сценария оформления заказа и тест на конкурентный резерв (без overselling).
- Покрытие меряем JaCoCo.

**0.10 CI.** GitHub Actions: `./mvnw verify`, затем сборка образов и публикация в GHCR. Обновление зависимостей через Renovate или Dependabot.

**0.11 Документация.** README, диаграмма архитектуры (mermaid/C4), каталог `docs/adr/` для архитектурных решений (ADR).

**Готово, когда:** весь проект на Java 25 / Boot 4 собирается `./mvnw verify` с зелёными Testcontainers-тестами, `docker compose --profile infra --profile apps up` поднимает рабочий магазин, а e2e-скрипт проходит путь «товар → корзина → заказ».

---

## Этап 1. Observability: OpenTelemetry + Grafana LGTM (≈1–2 недели)

Делаем раньше Kafka: на следующих этапах всё асинхронное сразу будет видно в трейсах.
- `spring-boot-starter-opentelemetry` (Boot 4) во всех сервисах, экспорт по OTLP в **OTel Collector**. Collector раздаёт данные в **Tempo**, **Prometheus** и **Loki**, всё смотрим в **Grafana**. Для старта хватит образа `grafana/otel-lgtm`, на этапе k8s разносим компоненты по отдельности.
- Структурированные JSON-логи (`logging.structured.format.console=ecs`), traceId и spanId попадают в каждую строку.
- Контекст трейса прокидываем через Gateway, HTTP-клиенты и (позже) Kafka.
- Метрики:
  - стандартные RED и JVM;
  - бизнесовые: `orders_created_total`, `stock_reservation_failed_total`, таймер длительности саги;
  - exemplars, чтобы переходить из метрики в трейс.
- Дашборды и алерты в Grafana (provisioning из git), переход из лога в трейс.
- **k6** load-test-скрипт для генерации трафика и проверки дашбордов под нагрузкой.

**Готово, когда:** запрос `POST /orders` виден в Tempo одним трейсом gateway → order → cart/product, а логи этого запроса находятся в Loki по traceId.

---

## Этап 2. Kafka и событийная архитектура, сага (≈2–3 недели)

- Kafka в режиме **KRaft** в compose, UI берём Kafka UI или Redpanda Console.
- Топики (ключ определяет порядок сообщений):
  - `order.events` (ключ orderId): OrderCreated, OrderConfirmed, OrderCancelled;
  - `inventory.events` (ключ orderId): StockReserved, StockReservationFailed, StockReleased;
  - `product.events` (ключ productId, compacted): ProductUpsert, ProductDeleted.
- **Хореографическая сага** вместо синхронной цепочки в `ProductReservationServiceImpl`:
  1. `POST /orders` создаёт заказ `PENDING` и отвечает `202` с orderId.
  2. Product-service резервирует весь заказ одной транзакцией и публикует StockReserved или Failed.
  3. Order-service переводит заказ в `CONFIRMED` или `CANCELLED`.
  4. На OrderConfirmed cart-service чистит корзину.
  5. Отмена заказа освобождает остатки (компенсация).
- **notification-service** (новый): подписан на order.events и отправляет письма в **Mailpit**.
- Надёжность:
  - идемпотентные консьюмеры (таблица `processed_events`);
  - `DefaultErrorHandler` + DLT, `@RetryableTopic`;
  - на продюсере `acks=all` и идемпотентность;
  - понимание consumer group, rebalancing и партиций.
- Наблюдаемость Kafka: observation на KafkaTemplate и listener (трейс проходит через Kafka), метрика consumer lag.
- Тесты: Testcontainers Kafka, Awaitility.

**Готово, когда:** заказ проходит сагу асинхронно, падение product-service не теряет заказ (после рестарта он доезжает), а при параллельных заказах на последний товар ровно один получает CONFIRMED.

---

## Этап 3. Transactional Outbox + Debezium, Schema Registry + Avro (≈2 недели)

- Проблема dual-write: сохранить в БД и отправить в Kafka нельзя атомарно.
- Решение в два шага. Сначала сам пишешь outbox-таблицу и poller (`@Scheduled`), чтобы понять механику. Потом заменяешь poller на **Debezium** (Kafka Connect, Postgres `wal_level=logical`, Outbox Event Router SMT).
- Модуль **events-contracts**: Avro-схемы (`.avsc`), классы генерирует `avro-maven-plugin`.
- **Schema Registry** (Confluent или Apicurio), режим совместимости BACKWARD. Отдельно отрабатываем эволюцию схемы: добавляем поле с default и проверяем, что старый консьюмер продолжает работать.
- Нюанс Outbox + Avro: payload хранится в outbox как bytea, уже сериализованный Avro-сериализатором. Debezium передаёт его через binary converter. Финальный вариант выбираем при реализации и фиксируем в ADR.

**Готово, когда:** в коде сервисов нет прямых `kafkaTemplate.send` для доменных событий, все события идут через outbox → Debezium, а схемы лежат в Registry.

---

## Этап 4. Полнотекстовый поиск: Elasticsearch (≈2 недели)

- **search-service** (новый) на Spring Data Elasticsearch. Индекс `products` заполняется из `product.events`: это CQRS read model.
- Маппинг:
  - анализатор `russian` (морфология) и стоп-слова;
  - `edge_ngram` для автодополнения;
  - `keyword`-поля brand и category для фасетов, `price` для range-фильтра.
- API:
  - поиск с fuzziness, подсветкой (highlight) и фасетами (aggregations), сортировкой по релевантности и цене;
  - `/suggest` для автодополнения.
- Переиндексация без простоя: новый индекс плюс переключение alias. Полная пересборка индекса из product-service.
- Gateway маршрутизирует `/api/v1/search/**`. LIKE-поиск в `ProductSpecification` остаётся простым фильтром либо удаляется.
- Тесты: Testcontainers Elasticsearch.

**Готово, когда:** «смартфоны самсунг» находит «Смартфон Samsung …», опечатка «samsng» тоже находит, фасеты считаются, а изменение товара доезжает в поиск через Kafka за секунды.

---

## Этап 5. Колоночная БД: ClickHouse, аналитика (≈1–2 недели)

- ClickHouse в compose.
- Ингест прямо из Kafka без кода: **Kafka Engine table** (`AvroConfluent` + Schema Registry), затем **Materialized View**, затем `MergeTree`, партиционирование по месяцу.
- Агрегаты через `SummingMergeTree` / `AggregatingMergeTree` MV: выручка по дням, топ товаров, средний чек, воронка (просмотр → корзина → заказ). Для воронки добавляем события `ProductViewed` и `CartItemAdded`.
- **analytics-service** (новый): REST-отчёты через `JdbcClient` + clickhouse-jdbc. Бизнес-дашборд в Grafana через ClickHouse datasource.
- Эксперимент для понимания колоночного хранения: сгенерировать ~10M событий и сравнить одни и те же агрегаты в Postgres и ClickHouse (время и объём на диске).

**Готово, когда:** после k6-прогона бизнес-дашборд в Grafana показывает выручку и топ товаров из ClickHouse, а эксперимент с цифрами описан в `docs/`.

---

## Этап 6. Безопасность: Keycloak / OAuth2 (≈1–2 недели)

- Keycloak в compose, realm `nanomarket` импортируется из json в репо. Роли CUSTOMER и ADMIN.
- Gateway работает как OAuth2 Resource Server (проверка JWT), сервисы тоже проверяют JWT (zero trust).
- Rate limiting по пользователю: Redis `RequestRateLimiter`.
- `customerId` и `cartId` берём из `sub` токена, а не из запроса. Миграция `customerId Long → UUID` в order и cart (Liquibase).
- CRUD товаров, брендов и категорий доступен только ADMIN.
- Межсервисные вызовы идут через client credentials.
- Тесты: `spring-security-test` (mock JWT) и Testcontainers Keycloak для e2e.

**Готово, когда:** без токена 401, CUSTOMER не может создать товар (403), а заказ создаётся только на свою корзину.

---

## Этап 7. Kubernetes (≈3–4 недели)

- Локальный кластер **k3d** (или kind) на Docker Desktop.
- Профиль `k8s`: Eureka и Config client выключены, вместо них Service DNS и ConfigMaps/Secrets. Actuator-пробы liveness/readiness, graceful shutdown.
- **Helm**: один общий chart для Spring-сервисов, свой values-файл на сервис.
  - Deployment, Service, ConfigMap, Secret;
  - resources, HPA, PodDisruptionBudget;
  - securityContext (non-root).
- Инфраструктура через операторы и официальные charts:
  - **Strimzi**: Kafka KRaft и KafkaConnect с Debezium;
  - **CloudNativePG**: Postgres;
  - **ECK**: Elasticsearch;
  - **Altinity ClickHouse operator**;
  - Keycloak и Redis через официальные charts;
  - Schema Registry.
- Observability в кластере: kube-prometheus-stack, Loki, Tempo, OpenTelemetry Operator.
- Входящий трафик через **Gateway API** (Envoy Gateway или Traefik). ingress-nginx выводится из поддержки, статус проверим на старте этапа.
- Dev loop через Skaffold или Tilt (опционально). CI собирает образы в GHCR и прогоняет `helm lint` / `helm template`.
- Тренировка эксплуатации: rolling update, kill pod (сага переживает), масштабирование консьюмеров и ребалансировка партиций.

**Готово, когда:** `helm install` поднимает весь магазин в k3d, e2e-сценарий проходит через Gateway API, а дашборды и трейсы работают внутри кластера.

---

## Ключевые файлы, которые затронем

- `pom.xml` (root) и `*/pom.xml`: сборка и версии.
- `*/Dockerfile`, `docker-compose.yml`, новые `.env.example` и `config-repo/`.
- `OrderService/src/main/java/my/project/orderservice/service/impl/ProductReservationServiceImpl.java`, `OrderServiceImpl.java`, `controller/OrderController.java`: сага.
- `OrderService/.../service/*FeignClient.java`, `CartService/.../service/ProductFeignClient.java`: переход на HTTP Service Clients.
- `ProductService/.../service/impl/ProductServiceImpl.java` (резерв), `controller/ProductController.java` (sort), `repository/specifications/ProductSpecification.java` (поиск).
- `CartService/.../service/impl/CartServiceImpl.java`, `entity/Cart.java`: очистка по событию, UUID пользователя.
- `*/src/main/resources/db/changelog/db.changelog-master.yaml`: outbox, `processed_events`, `@Version`, UUID.
- Новые модули: `events-contracts/`, `SearchService/`, `AnalyticsService/`, `NotificationService/`, а также `deploy/helm/`, `deploy/k8s/`, `observability/`, `docs/adr/`.

## Проверка (сквозная, после каждого этапа)

1. `./mvnw verify`: unit-тесты и Testcontainers-интеграционные тесты зелёные.
2. `docker compose --profile infra --profile apps --profile observability up -d`, все контейнеры healthy.
3. E2E-сценарий (Connekt-скрипт или k6):
   - логин в Keycloak;
   - ADMIN создаёт товар, товар находится через search;
   - CUSTOMER кладёт товар в корзину и оформляет заказ;
   - заказ становится CONFIRMED, корзина пуста, письмо пришло в Mailpit, продажа есть в ClickHouse.
4. В Grafana: один трейс через gateway → order → Kafka → product, логи находятся по traceId, consumer lag ≈ 0.
5. На Этапе 7 тот же сценарий проходит в k3d.

## Как это подать в резюме

На каждый этап пишется ADR и раздел в README с диаграммой. В резюме формулируем решённые задачи, а не перечень технологий. Например:
- «устранил overselling и dual-write через сагу + transactional outbox (Debezium)»;
- «полнотекстовый поиск с морфологией и фасетами на Elasticsearch, CQRS read model из Kafka»;
- «аналитика на ClickHouse с ингестом из Kafka»;
- «сквозная трассировка на OpenTelemetry»;
- «деплой в Kubernetes через Helm и операторы».

**Порядок работы:** начинаем с Этапа 0, шаг 0.1. Каждый шаг идёт отдельной веткой и PR, коммиты делаю только по твоей команде.
