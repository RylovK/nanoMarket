package my.project.productservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.project.productservice.dto.ProductReservationRequest;
import my.project.productservice.messaging.events.OrderCreatedEvent;
import my.project.productservice.messaging.events.ProductOutOfStockEvent;
import my.project.productservice.messaging.events.ProductReservedEvent;
import my.project.productservice.persistence.entity.ProcessedEvent;
import my.project.productservice.persistence.repository.ProcessedEventRepository;
import my.project.productservice.service.ProductService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@KafkaListener(
        topics = "${spring.kafka.topic.orderCreated}",
        containerFactory = "orderCreatedKafkaListenerContainerFactory")
public class OrderCreatedListener {

    private final ProductService productService;
    private final ProcessedEventRepository processedEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsConfig kafkaTopicsConfig;


    @Transactional
    @KafkaHandler
    public void handleOrderCreatedEvent(@Payload OrderCreatedEvent event,
                                        @Header("messageId") String messageId,
                                        @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {
        log.info("Received order created event: {}", event);
        log.info("Message Id: {}", messageId);
        log.info("Message Key: {}", messageKey);
        Optional<ProcessedEvent> foundedMessage = processedEventRepository.findByMessageId(messageId);
        if (foundedMessage.isPresent()) {
            log.info("Found duplicate event for messageId: {}", messageId);
            return;
        }

        List<ProductReservationRequest> requestList = event.items();
        if (productService.reserveProducts(requestList)) {
            log.info("Reserved product list: {}", requestList);
            ProductReservedEvent reservedEvent = new ProductReservedEvent(event.orderId());
            kafkaTemplate.send(kafkaTopicsConfig.getProductReserved(), reservedEvent);
        } else {
            ProductOutOfStockEvent outOfStockEvent = new ProductOutOfStockEvent(event.orderId(), "Not enough products");
            kafkaTemplate.send(kafkaTopicsConfig.getProductOutOfStock(), outOfStockEvent);
        }
        try {
            processedEventRepository.save(new ProcessedEvent(messageId, event.orderId().toString()));
            log.info("Saved to db {}: {}", messageId, event);
        } catch (DataIntegrityViolationException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
