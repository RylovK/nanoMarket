package my.project.orderservice.messaging;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kafka.topic")
@Getter
@Setter
public class KafkaTopicsConfig {
    private String orderCreated;
    private String orderCancelled;
    private String productReserved;
    private String productOutOfStock;
}