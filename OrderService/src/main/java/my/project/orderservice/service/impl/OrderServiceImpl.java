package my.project.orderservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.project.orderservice.dto.*;
import my.project.orderservice.messaging.KafkaTopicsConfig;
import my.project.orderservice.messaging.events.OrderCreatedEvent;
import my.project.orderservice.entity.OrderEntity;
import my.project.orderservice.mapper.OrderMapper;
import my.project.orderservice.repository.*;
import my.project.orderservice.service.OrderService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    private final KafkaTopicsConfig kafkaTopicsConfig;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderEntity orderEntity) throws ExecutionException, InterruptedException {
        orderEntity.setStatus(OrderEntity.Status.PENDING);
        OrderEntity saved = orderRepository.save(orderEntity);

        log.info("Order {} created for customer: {}", saved.getId(), saved.getCustomerId());

        OrderCreatedEvent event = orderMapper.toOrderCreatedEvent(saved);

                log.debug("Sending order created event to Kafka for order: {}", saved.getId());

        ProducerRecord<String, OrderCreatedEvent> producerRecord = new ProducerRecord<>(
                kafkaTopicsConfig.getOrderCreated(),
                String.valueOf(saved.getCustomerId()),
                event
        );
        producerRecord.headers().add("messageId", UUID.randomUUID().toString().getBytes());

        SendResult<String, OrderCreatedEvent> result = kafkaTemplate
                .send(producerRecord).get();

        log.debug("Order created event sent to Kafka: {}", result.getRecordMetadata());

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
