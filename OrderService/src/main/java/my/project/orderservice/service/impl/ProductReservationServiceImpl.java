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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductReservationServiceImpl implements ProductReservationService {

    private final ProductFeignClient productFeignClient;
    private final CartFeignClient cartFeignClient;


    @Bulkhead(name = "checkStockAndReserveProducts", type = Bulkhead.Type.THREADPOOL,
            fallbackMethod = "fallbackCheckStockAndReserveProducts")
    @CircuitBreaker(name = "checkStockAndReserveProducts",
            fallbackMethod = "fallbackCheckStockAndReserveProducts")

    @Retry(name = "checkStockAndReserveProducts",
            fallbackMethod = "fallbackCheckStockAndReserveProducts")
    @Override
    public OrderEntity checkStockAndReserveProducts(OrderRequest orderRequest) {
        var customerId = orderRequest.customerId();
        var orderEntity = new OrderEntity(customerId);
        var cart = validateAndGetCart(customerId);

        var reservationRequestList = cart.getItems()
                .entrySet()
                .stream()
                .map(entry -> {
                    checkStockAndAddToOrder(entry, orderEntity);
                    return createProductReservationRequest(entry);
                })
                .toList();

        reserveProducts(reservationRequestList);
        log.info("Successfully reserved products for customer: {}", customerId);
        cartFeignClient.clearCart(customerId);

        return orderEntity;
    }
    /**
     * Validates the stock availability for a cart item and adds it to the order.
     *
     * @param cartItem   the cart item containing the product ID and quantity
     * @param orderEntity the order to which the item should be added
     * @throws IllegalArgumentException if there is insufficient stock
     */
    private void checkStockAndAddToOrder(Map.Entry<Long, Integer> cartItem, OrderEntity orderEntity) {
        Long productId = cartItem.getKey();
        Integer quantityToOrder = cartItem.getValue();
        Long customerId = orderEntity.getCustomerId();
        log.debug("Getting availability of product {} for customer {}", productId, customerId);
        var availability = getProductAvailability(productId);

        if (availability.quantity() < quantityToOrder) {
            log.warn("Not enough stock for product {} for customer {}", productId, customerId);
            throw new IllegalArgumentException("Not enough stock");//TODO: specify Exception
        }
        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setQuantity(quantityToOrder);
        item.setPrice(availability.price());
        item.setOrder(orderEntity);
        orderEntity.getItems().add(item);

        BigDecimal total = orderEntity.getTotal().add(calculateTotal(availability.price(), quantityToOrder));
        orderEntity.setTotal(total);

        log.debug("Product {} with quantity {} added to order for customer {}", productId, quantityToOrder, customerId);
    }

    /**
     * Creates a {@link ProductReservationRequest} for the given cart item.
     *
     * @param cartItem the cart item containing the product ID and quantity
     * @return a {@link ProductReservationRequest} for the product
     */
    private ProductReservationRequest createProductReservationRequest(Map.Entry<Long, Integer> cartItem) {
        Long productId = cartItem.getKey();
        Integer quantityToOrder = cartItem.getValue();
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

    @Bulkhead(name = "reserveProducts", type = Bulkhead.Type.THREADPOOL,
            fallbackMethod = "fallbackReserveProducts")
    @CircuitBreaker(name = "reserveProducts",
            fallbackMethod = "fallbackReserveProducts")
    @Retry(name = "reserveProducts",
            fallbackMethod = "fallbackReserveProducts")
    private void reserveProducts(List<ProductReservationRequest> reservationRequestList) {
        productFeignClient.reserveProducts(reservationRequestList);
    }

    @Bulkhead(name = "getProductAvailability", type = Bulkhead.Type.THREADPOOL,
            fallbackMethod = "fallbackGetProductAvailability")
    @CircuitBreaker(name = "getProductAvailability",
            fallbackMethod = "fallbackGetProductAvailability")
    @Retry(name = "getProductAvailability",
            fallbackMethod = "fallbackGetProductAvailability")
    private ProductAvailabilityDTO getProductAvailability(Long productId) {
        return productFeignClient.getProductAvailability(productId);
    }

    public OrderEntity fallbackCheckStockAndReserveProducts(OrderRequest orderRequest, Throwable throwable) {
        log.error("Fallback for checkStockAndReserveProducts. OrderRequest: {}, Error: {}", orderRequest, throwable.getMessage(), throwable);
        OrderEntity fallbackOrder = new OrderEntity();
        fallbackOrder.setCustomerId(orderRequest.customerId());
        fallbackOrder.setItems(Collections.emptyList());
        fallbackOrder.setTotal(BigDecimal.ZERO);
        return fallbackOrder;
    }

    //TODO: резервная реализация должна предусматривать действия
    public void fallbackReserveProducts(List<ProductReservationRequest> reservationRequestList, Throwable throwable) {
        log.error("Fallback for reserveProducts triggered. Error: {}", throwable.getMessage(), throwable);
        throw new IllegalStateException("Fallback for reserveProducts triggered. Error: " + throwable.getMessage(), throwable);
    }

    //TODO: резервная реализация должна предусматривать действия
    public ProductAvailabilityDTO fallbackGetProductAvailability(Long productId, Throwable throwable) {
        log.error("Fallback for getProductAvailability triggered for product {}. Error: {}", productId, throwable.getMessage(), throwable);
        throw new IllegalStateException("Failed to get product availability for product: " + productId);
    }
}
