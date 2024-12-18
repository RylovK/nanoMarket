package my.project.orderservice.service.impl;

import lombok.RequiredArgsConstructor;
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
public class ProductReservationServiceImpl implements ProductReservationService {

    private final ProductFeignClient productFeignClient;
    private final CartFeignClient cartFeignClient;

    @Override
    public Order checkStockAndReserveProducts(OrderRequest orderRequest) {
        Order order = new Order();
        order.setCustomerId(orderRequest.getCustomerId());

        List<ProductReservationRequest> reservationRequestList = new ArrayList<>();

        Cart cart = cartFeignClient.getCart(orderRequest.getCustomerId());
        Map<Long, Integer> items = cart.getItems();
        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();
            ProductAvailabilityDTO availability = productFeignClient.getProductAvailability(productId);
            if (availability.getQuantity() < quantity) {
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
            }
        }
        productFeignClient.reserveProducts(reservationRequestList);
        cartFeignClient.clearCart(orderRequest.getCustomerId());
        return order;
    }
}
