package my.project.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.orderservice.dto.OrderDTO;
import my.project.orderservice.mapper.OrderMapper;
import my.project.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderMapper orderMapper;
    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable UUID orderId) {
        return null;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<OrderDTO> getOrderByUserId(@PathVariable Long userId) {
        return null;
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody @Valid OrderDTO orderDTO) {
        return null;
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable UUID orderId,
                                                @RequestBody @Valid OrderDTO orderDTO) {
        return null;
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<OrderDTO> deleteOrder(@PathVariable UUID orderId) {
        return null;
    }
}
