package my.project.orderservice.service.impl;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.project.orderservice.dto.Cart;
import my.project.orderservice.dto.OrderRequest;
import my.project.orderservice.dto.ProductAvailabilityDTO;
import my.project.orderservice.dto.ProductReservationRequest;
import my.project.orderservice.entity.OrderEntity;
import my.project.orderservice.entity.OrderItem;
import my.project.orderservice.service.CartFeignClient;
import my.project.orderservice.service.ProductFeignClient;
import my.project.orderservice.service.ProductReservationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductReservationServiceImpl implements ProductReservationService {

    private final ProductFeignClient productFeignClient;
    private final CartFeignClient cartFeignClient;

    @Override
//    @RateLimiter(name = "checkStockAndReserveProducts",
//            fallbackMethod = "fallbackCheckStockAndReserveProducts")
    @Bulkhead(name = "checkStockAndReserveProducts", type = Bulkhead.Type.THREADPOOL,
            fallbackMethod = "fallbackCheckStockAndReserveProducts")
    @CircuitBreaker(name = "checkStockAndReserveProducts",
            fallbackMethod = "fallbackCheckStockAndReserveProducts")

    @Retry(name = "checkStockAndReserveProducts",
            fallbackMethod="fallbackCheckStockAndReserveProducts")

    public OrderEntity checkStockAndReserveProducts(OrderRequest orderRequest) {
        Long customerId = orderRequest.getCustomerId();
        log.info("Checking stock and reserve products for customer: {}", customerId);
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setCustomerId(customerId);


        List<ProductReservationRequest> reservationRequestList = new ArrayList<>();

        Cart cart = cartFeignClient.getCart(customerId);
        Map<Long, Integer> items = cart.getItems();
        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();
            log.debug("Getting availability for productId: {}", productId);
            ProductAvailabilityDTO availability = productFeignClient.getProductAvailability(productId);
            if (availability.getQuantity() < quantity) {
                log.warn("Not enough stock for product {} in order {}", productId, customerId);
                throw new IllegalArgumentException("Not enough stock");
            } else {
                OrderItem item = new OrderItem();
                item.setProductId(productId);
                item.setQuantity(quantity);
                item.setPrice(availability.getPrice());
                item.setOrderEntity(orderEntity);
                orderEntity.getItems().add(item);
                orderEntity.setTotal(orderEntity.getTotal().add(availability.getPrice()));

                ProductReservationRequest request = new ProductReservationRequest();
                request.setProductId(productId);
                request.setQuantity(quantity);
                reservationRequestList.add(request);
                log.debug("Product {} with quantity {} added to order and reservation list for customer {}", productId, quantity, customerId);
            }
        }
        productFeignClient.reserveProducts(reservationRequestList);
        log.info("Successfully reserved products for customer: {}", customerId);
        cartFeignClient.clearCart(customerId);
        return orderEntity;
    }
    //TODO: резервная реализация должна предусматривать действия
    //для выполнения, когда срок ожидания ответа ресурса истек
    //или он вернул ошибку. Если резервная реализация перехваты-
    //вает исключение тайм-аута и просто регистрирует ошибки в журна-
    //ле, то вместо этой стратегии лучше вызывать службу в стандартном
    //блоке try ... catch;
    public OrderEntity fallbackCheckStockAndReserveProducts(OrderRequest orderRequest, Throwable throwable) {
        log.error("Fallback for checkStockAndReserveProducts. OrderRequest: {}, Error: {}", orderRequest, throwable.getMessage(), throwable);
        throw new RuntimeException("Fallback for checkStockAndReserveProducts due to: {}", throwable);
    }

    /**
     *
     public Order circuitBreakerFallback(OrderRequest orderRequest, Throwable throwable) {
     log.error("CircuitBreaker triggered for order: {}", orderRequest, throwable);
     return fallbackOrderLogic(orderRequest);
     }

     public Order rateLimiterFallback(OrderRequest orderRequest, Throwable throwable) {
     log.error("RateLimiter triggered for order: {}", orderRequest, throwable);
     return fallbackOrderLogic(orderRequest);
     }

     public Order retryFallback(OrderRequest orderRequest, Throwable throwable) {
     log.error("Retry exhausted for order: {}", orderRequest, throwable);
     return fallbackOrderLogic(orderRequest);
     }

     public Order bulkheadFallback(OrderRequest orderRequest, Throwable throwable) {
     log.error("Bulkhead limit reached for order: {}", orderRequest, throwable);
     return fallbackOrderLogic(orderRequest);
     }

     private Order fallbackOrderLogic(OrderRequest orderRequest) {
     Order fallbackOrder = new Order();
     fallbackOrder.setCustomerId(orderRequest.getCustomerId());
     fallbackOrder.setItems(Collections.emptyList());
     fallbackOrder.setTotal(BigDecimal.ZERO);
     return fallbackOrder;
     }

     */
}
