package my.project.commands;

import my.project.dto.OrderItemDTO;

import java.util.List;
import java.util.UUID;

public record ReserveProductCommand (UUID orderId,
                                    List<OrderItemDTO> items) {
}
