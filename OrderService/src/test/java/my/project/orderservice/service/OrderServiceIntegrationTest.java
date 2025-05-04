//package my.project.orderservice.service;
//
//import my.project.orderservice.dto.OrderDTO;
//import my.project.orderservice.entity.OrderEntity;
//import my.project.orderservice.messaging.KafkaTopicsConfig;
//import my.project.events.OrderCreatedEvent;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.env.Environment;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.listener.ContainerProperties;
//import org.springframework.kafka.listener.KafkaMessageListenerContainer;
//import org.springframework.kafka.listener.MessageListener;
//import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//import org.springframework.kafka.test.EmbeddedKafkaBroker;
//import org.springframework.kafka.test.context.EmbeddedKafka;
//import org.springframework.kafka.test.utils.ContainerTestUtils;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.ArrayList;
//import java.util.Map;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.*;
////
////@DirtiesContext
////@TestInstance(TestInstance.Lifecycle.PER_CLASS)
////@ActiveProfiles("test")
////@EmbeddedKafka(partitions = 3, count = 3, controlledShutdown = true)
////@SpringBootTest(properties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}", "spring.cloud.config.enabled=false"})
//class OrderServiceIntegrationTest {
//
//    @Autowired
//    private EmbeddedKafkaBroker embeddedKafkaBroker;
//
//    @Autowired
//    private Environment env;
//
//    @Autowired
//    private OrderService orderService;
//
//    @Autowired
//    private KafkaTopicsConfig kafkaTopicsConfig;
//
//    private KafkaMessageListenerContainer<String, OrderCreatedEvent> container;
//
//    private BlockingQueue<ConsumerRecord<String, OrderCreatedEvent>> records;
//
//    @BeforeAll
//    void setUp() {
//        DefaultKafkaConsumerFactory<String, Object> consumerFactory = new DefaultKafkaConsumerFactory<>(getConsumerConfigs());
//        ContainerProperties containerProps = new ContainerProperties(kafkaTopicsConfig.getOrderCreated());
//        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProps);
//        records = new LinkedBlockingQueue<>();
//        container.setupMessageListener((MessageListener<String, OrderCreatedEvent>) records::add);
//        container.start();
//        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
//    }
//
//    private Map<String, Object> getConsumerConfigs() {
//        return Map.of(
//                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
//                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
//                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
//                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class,
//                JsonDeserializer.VALUE_DEFAULT_TYPE, OrderCreatedEvent.class.getName(),
//                ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("spring.kafka.consumer.group-id"),
//                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, env.getProperty("spring.kafka.consumer.auto-offset-reset"),
//                JsonDeserializer.TRUSTED_PACKAGES, "my.project.orderservice.messaging.events"
//        );
//    }
//
//    @Test
//    void testCreateOrder_WhenGivenValidDetails_successfullySendsKafkaMessage() throws InterruptedException {
//        //Arrange
//        OrderEntity orderEntity = new OrderEntity(1L);
////        orderEntity.setId(1L);
//        orderEntity.setStatus(OrderEntity.Status.PENDING);
//        orderEntity.setItems(new ArrayList<>());
//
//        //Act
//        OrderDTO createdOrderDTO = orderService.createOrder(orderEntity);
//        //Assert
//        ConsumerRecord<String, OrderCreatedEvent> received = records.poll(3000, TimeUnit.MILLISECONDS);
//        assertNotNull(received, "No message received");
//        assertNotNull(received.key(), "No key received");
//        OrderCreatedEvent event = received.value();
//        assertEquals(createdOrderDTO.getId(), event.orderId(), "Order ID does not match");
//    }
//
//    @AfterAll
//    void tearDown() {
//        container.stop();
//    }
//}