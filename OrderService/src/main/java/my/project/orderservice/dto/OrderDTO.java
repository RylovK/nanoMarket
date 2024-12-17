package my.project.orderservice.dto;

import lombok.Getter;
import lombok.Setter;
import my.project.orderservice.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class OrderDTO {

    private String orderId;
    private Long customerId;
    private BigDecimal total;
    private Order.Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDTO> items;
}
