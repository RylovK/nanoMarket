package my.project.productservice.messaging;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka.topic")
@Getter
@Setter
public class KafkaTopicsConfig {
    private String orderCreated;
    private String orderCancelled;
    private String productReserved;
    private String productOutOfStock;
}