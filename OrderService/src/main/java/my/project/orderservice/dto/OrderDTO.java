package my.project.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import my.project.orderservice.entity.OrderEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Data transfer object (DTO) representing an order.
 * Contains details about the order, including customer information, total amount,
 * order status, and the list of order items.
 */
@Getter @Setter
@AllArgsConstructor
public class OrderDTO {

    private UUID id;
    private Long customerId;
    private BigDecimal total;
    private OrderEntity.Status status;
    private List<OrderItemDTO> items;
}
