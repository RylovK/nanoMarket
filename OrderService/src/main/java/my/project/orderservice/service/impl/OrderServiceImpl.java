package my.project.orderservice.service.impl;

import lombok.RequiredArgsConstructor;
import my.project.orderservice.dto.*;
import my.project.orderservice.entity.Order;
import my.project.orderservice.entity.OrderItem;
import my.project.orderservice.mapper.OrderMapper;
import my.project.orderservice.repository.OrderRepository;
import my.project.orderservice.service.CartFeignClient;
import my.project.orderservice.service.OrderService;
import my.project.orderservice.service.ProductFeignClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CartFeignClient cartFeignClient;
    private final ProductFeignClient productFeignClient;

    @Transactional
    @Override
    public OrderDTO createOrder(OrderRequest orderRequest) {
        Order order = new Order();
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
                ProductReservationRequest request = new ProductReservationRequest();
                OrderItem item = new OrderItem();

                item.setProductId(productId);
                item.setQuantity(quantity);
                item.setPrice(availability.getPrice());
                item.setOrder(order);
                order.getItems().add(item);

                request.setProductId(productId);
                request.setQuantity(quantity);
                reservationRequestList.add(request);
            }
        }
        return null;
    }
}
