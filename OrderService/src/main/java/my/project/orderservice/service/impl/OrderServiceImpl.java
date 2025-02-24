package my.project.orderservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.project.orderservice.dto.*;
import my.project.orderservice.entity.OrderEntity;
import my.project.orderservice.mapper.OrderMapper;
import my.project.orderservice.repository.OrderRepository;
import my.project.orderservice.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderEntity orderEntity) {
        OrderEntity saved = orderRepository.save(orderEntity);
        log.info("Order {} created for customer: {}", saved.getId(), saved.getCustomerId());
        return orderMapper.toOrderDTO(saved);
    }

    @Override
    public OrderDTO getOrderById(UUID orderId) {
        return orderMapper.toOrderDTO(orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public List<OrderDTO> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(orderMapper::toOrderDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(UUID orderId, OrderEntity.Status newStatus) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        orderEntity.setStatus(newStatus);
        //TODO: add logic for statuses processing(message sending?)
        return orderMapper.toOrderDTO(orderRepository.save(orderEntity));
    }

    @Override
    @Transactional
    public boolean deleteOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(orderEntity -> {
                    orderEntity.setStatus(OrderEntity.Status.DELETED);
                    orderRepository.save(orderEntity);
                    return true;
                })
                .orElse(false);
    }
}
