package my.project.orderservice.service.impl;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.project.orderservice.dto.Cart;
import my.project.orderservice.dto.OrderRequest;
import my.project.orderservice.dto.ProductReservationRequest;
import my.project.orderservice.entity.OrderEntity;
import my.project.orderservice.entity.OrderItem;
import my.project.orderservice.service.CartFeignClient;
import my.project.orderservice.service.ProductFeignClient;
import my.project.orderservice.service.ProductReservationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductReservationServiceImpl implements ProductReservationService {

    private final ProductFeignClient productFeignClient;
    private final CartFeignClient cartFeignClient;

    @Override
    @Bulkhead(name = "checkStockAndReserveProducts", type = Bulkhead.Type.THREADPOOL,
            fallbackMethod = "fallbackCheckStockAndReserveProducts")
    @CircuitBreaker(name = "checkStockAndReserveProducts",
            fallbackMethod = "fallbackCheckStockAndReserveProducts")

    @Retry(name = "checkStockAndReserveProducts",
            fallbackMethod = "fallbackCheckStockAndReserveProducts")

    public OrderEntity checkStockAndReserveProducts(OrderRequest orderRequest) {
        var customerId = orderRequest.customerId();
        var orderEntity = new OrderEntity(customerId);
        var cart = validateAndGetCart(customerId);

        var reservationRequestList = cart.getItems()
                .entrySet()
                .stream()
                .map(entry -> checkStockAndAddToOrder(entry, orderEntity))
                .toList();

        productFeignClient.reserveProducts(reservationRequestList);
        log.info("Successfully reserved products for customer: {}", customerId);
        cartFeignClient.clearCart(customerId);

        return orderEntity;
    }

    private ProductReservationRequest checkStockAndAddToOrder(Map.Entry<Long, Integer> cartItem, OrderEntity orderEntity) {
        Long productId = cartItem.getKey();
        Integer quantityToOrder = cartItem.getValue();
        Long customerId = orderEntity.getCustomerId();
        log.debug("Getting availability of productId for customer {}: {}", productId, customerId);
        var availability = productFeignClient.getProductAvailability(productId);

        if (availability.quantity() < quantityToOrder) {
            log.warn("Not enough stock for product {} for customer {}", productId, customerId);
            throw new IllegalArgumentException("Not enough stock");
        }
        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setQuantity(quantityToOrder);
        item.setPrice(availability.price());
        item.setOrder(orderEntity);

        orderEntity.getItems().add(item);

        BigDecimal total = orderEntity.getTotal().add(calculateTotal(availability.price(), quantityToOrder));
        orderEntity.setTotal(total);

        log.debug("Product {} with quantity {} added to order and reservation list for customer {}", productId, quantityToOrder, customerId);

        return new ProductReservationRequest(productId, quantityToOrder);
    }


    /**
     * Retrieves the cart for the specified customer and validates that it is not empty.
     *
     * @param customerId the ID of the customer whose cart needs to be validated
     * @return a {@link Cart} containing the items for the specified customer
     * @throws IllegalStateException if the cart is empty or not found
     */
    private Cart validateAndGetCart(Long customerId) {
        log.debug("Retrieving cart for customer with ID: {}", customerId);
        Cart cart = cartFeignClient.getCart(customerId);
        if (cart.getItems().isEmpty()) {
            log.warn("Cart is empty or not found for customer with ID: {}", customerId);
            //TODO: need to create exception
            throw new IllegalStateException("No cart items found for customer: " + customerId);
        }
        log.debug("Cart retrieved successfully for customer with ID: {}", customerId);
        return cart;
    }

    private BigDecimal calculateTotal(BigDecimal price, Integer quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
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
