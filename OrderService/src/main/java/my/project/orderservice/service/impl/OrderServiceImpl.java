package my.project.orderservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import my.project.orderservice.dto.*;
import my.project.orderservice.entity.Order;
import my.project.orderservice.mapper.OrderMapper;
import my.project.orderservice.repository.OrderRepository;
import my.project.orderservice.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDTO createOrder(Order order) {
        Order saved = orderRepository.save(order);
        return orderMapper.toOrderDTO(saved);
    }

    @Override
    public OrderDTO getOrderById(UUID orderId) {
        return orderMapper.toOrderDTO(orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public List<OrderDTO> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(orderMapper::toOrderDTO)
                .collect(Collectors.toList());
    }
}
