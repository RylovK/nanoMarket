package my.project.orderservice.service;

import jakarta.validation.Valid;
import my.project.orderservice.dto.OrderDTO;
import my.project.orderservice.entity.OrderEntity;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderDTO createOrder(@Valid OrderEntity orderEntity);

    OrderDTO getOrderById(UUID orderId);

    List<OrderDTO> getOrdersByCustomerId(Long userId);

    OrderDTO updateOrderStatus(UUID orderId, OrderEntity.Status newStatus);

    boolean deleteOrder(UUID orderId);
}
