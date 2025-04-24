package my.project.orderservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.project.orderservice.dto.OrderDTO;
import my.project.orderservice.entity.OrderEntity;
import my.project.orderservice.messaging.events.OrderCancelledEvent;
import my.project.orderservice.messaging.events.ProductOutOfStockEvent;
import my.project.orderservice.messaging.events.ProductReservedEvent;
import my.project.orderservice.service.CartFeignClient;
import my.project.orderservice.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductInventoryListener {

    private final OrderService orderService;
    private final CartFeignClient cartFeignClient;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsConfig kafkaTopicsConfig;


    @KafkaListener(
            topics = "${spring.kafka.topic.productReserved}",
    containerFactory = "productReservedListenerContainerFactory"
    )
    @Transactional
    public void handleProductReservedEvent(ProductReservedEvent event) {
        log.info("Handling product reserved event: {}", event);

        OrderDTO updated = orderService.updateOrderStatus(event.orderId(), OrderEntity.Status.CONFIRMED);

        Long customerId = updated.getCustomerId();
        cartFeignClient.clearCart(customerId);

        log.info("Cart cleared for customer {}", customerId);
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.productOutOfStock}",
            containerFactory = "outOfStockEventListenerContainerFactory"
    )
    @Transactional
    public void handleProductOutOfStockEvent(ProductOutOfStockEvent event) {
        log.info("Handling product out of stock event: {}", event);

        OrderDTO updatedOrder = orderService.updateOrderStatus(event.orderId(), OrderEntity.Status.CANCELLED);
        OrderCancelledEvent cancelledEvent = new OrderCancelledEvent(updatedOrder.getId(), updatedOrder.getCustomerId(), "Not enough stock");
        kafkaTemplate.send(kafkaTopicsConfig.getOrderCancelled(), String.valueOf(updatedOrder.getCustomerId()), cancelledEvent);
    }
}
