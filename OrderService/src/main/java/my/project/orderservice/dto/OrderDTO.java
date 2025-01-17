package my.project.orderservice.dto;

import lombok.Getter;
import lombok.Setter;
import my.project.orderservice.entity.OrderEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class OrderDTO {

    private UUID orderId;
    private Long customerId;
    private BigDecimal total;
    private OrderEntity.Status status;
    private List<OrderItemDTO> items;
}
