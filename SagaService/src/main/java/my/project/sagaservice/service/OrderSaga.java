package my.project.sagaservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.project.events.OrderCreatedEvent;
import my.project.commands.ReserveProductCommand;
import my.project.sagaservice.config.KafkaTopicsConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(
        topics = "${spring.kafka.topic.orderEvents}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "orderCreatedEventConcurrentKafkaListenerContainerFactory"
)
@RequiredArgsConstructor
@Slf4j
public class OrderSaga {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsConfig kafkaTopicsConfig;

    @KafkaHandler
    public void handleOrderCreatedEvent(@Payload OrderCreatedEvent event,
                                        @Header("messageId") String messageId,
                                        @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {

        log.info("Handling order created event: {}", event);
        ReserveProductCommand reserveProductCommand = new ReserveProductCommand(
                event.orderId(), event.items());

        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(
                kafkaTopicsConfig.getProductCommands(),
                messageKey,
                reserveProductCommand
        );

        producerRecord.headers().add("messageId", messageId.getBytes());

        kafkaTemplate.send(producerRecord)
                .thenAccept(
                        result -> log.info("Sent reserve product command: {}", result.getRecordMetadata()))
                .exceptionally(ex -> {
                    log.error("Failed to send reserve product command: {}", ex.getMessage());
                    return null;
                });


    }
}
