package my.project.orderservice.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.project.orderservice.dto.Cart;
import my.project.orderservice.dto.OrderRequest;
import my.project.orderservice.dto.ProductAvailabilityDTO;
import my.project.orderservice.dto.ProductReservationRequest;
import my.project.orderservice.entity.Order;
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
    @CircuitBreaker(name = "checkStockAndReserveProducts", fallbackMethod = "fallbackCheckStockAndReserveProducts")
    public Order checkStockAndReserveProducts(OrderRequest orderRequest) {
        Long customerId = orderRequest.getCustomerId();
        log.info("Checking stock and reserve products for customer: {}", customerId);
        Order order = new Order();
        order.setCustomerId(customerId);

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
                item.setOrder(order);
                order.getItems().add(item);
                order.setTotal(order.getTotal().add(availability.getPrice()));

                ProductReservationRequest request = new ProductReservationRequest();
                request.setProductId(productId);
                request.setQuantity(quantity);
                reservationRequestList.add(request);
                log.debug("Product {} with quantity {} added to order and reservation list for customer {}", productId, quantity, customerId);
            }
        }
        productFeignClient.reserveProducts(reservationRequestList);
        log.info("Successfully Reserved products for customer: {}", customerId);
        cartFeignClient.clearCart(customerId);
        return order;
    }

    public Order fallbackCheckStockAndReserveProducts(OrderRequest orderRequest, Throwable throwable) {
        log.error("Fallback for checkStockAndReserveProducts due to: {}", throwable.getMessage());
        return null;
    }
}
