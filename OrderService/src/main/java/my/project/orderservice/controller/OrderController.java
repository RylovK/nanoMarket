package my.project.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.orderservice.dto.OrderDTO;
import my.project.orderservice.dto.OrderRequest;
import my.project.orderservice.entity.OrderEntity;
import my.project.orderservice.service.OrderService;
import my.project.orderservice.service.ProductReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final ProductReservationService productReservationService;
    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable UUID orderId) {
        var founded = orderService.getOrderById(orderId);
        return ResponseEntity.ok(founded);
    }

    @GetMapping("/user/{customerId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomerId(@PathVariable Long customerId) {
        var ordersList = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(ordersList);
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody @Valid OrderRequest orderRequest) {
        var orderEntity = productReservationService.checkStockAndReserveProducts(orderRequest);
        var dto = orderService.createOrder(orderEntity);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable UUID orderId,
                                                      @RequestBody OrderEntity.Status newStatus) {
        var updated = orderService.updateOrderStatus(orderId, newStatus);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID orderId) {
        if (orderService.deleteOrder(orderId)) {
            return ResponseEntity.ok().build();
        } else return ResponseEntity.notFound().build();
    }
}
