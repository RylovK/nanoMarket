package my.project.orderservice.service;

import jakarta.validation.Valid;
import my.project.orderservice.dto.OrderDTO;
import my.project.orderservice.entity.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderDTO createOrder(@Valid Order order);

    OrderDTO getOrderById(UUID orderId);

    List<OrderDTO> getOrdersByCustomerId(Long userId);
}
