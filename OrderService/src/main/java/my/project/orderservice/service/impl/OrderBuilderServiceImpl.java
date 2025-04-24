package my.project.orderservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.project.orderservice.dto.Cart;
import my.project.orderservice.entity.OrderEntity;
import my.project.orderservice.entity.OrderItem;
import my.project.orderservice.mapper.OrderMapper;
import my.project.orderservice.service.CartFeignClient;
import my.project.orderservice.service.OrderBuilderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderBuilderServiceImpl implements OrderBuilderService {

    private final CartFeignClient cartFeignClient;
    private final OrderMapper orderMapper;

    @Override
    public OrderEntity buildOrderFromCart(Long customerId) {
        Cart cart = validateAndGetCart(customerId);

        OrderEntity orderEntity = new OrderEntity(customerId);

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(orderMapper::cartItemToOrderItem)
                .map(orderItem -> {
                    orderItem.setOrder(orderEntity);
                    return orderItem;
                })
                .toList();

        BigDecimal total = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        orderEntity.setItems(orderItems);
        orderEntity.setTotal(total);

        return orderEntity;
    }

    private Cart validateAndGetCart(Long customerId) {
        log.debug("Retrieving cart for customer: {}", customerId);
        Cart cart = cartFeignClient.getCart(customerId);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("No items in cart for customer: " + customerId);
        }
        return cart;
    }
}
