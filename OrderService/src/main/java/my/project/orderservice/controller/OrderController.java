package my.project.orderservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Order API", description = "API for managing orders")
public class OrderController {

    private final ProductReservationService productReservationService;
    private final OrderService orderService;

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID",
            description = "Retrieve details of an order by its unique identifier")
    public ResponseEntity<OrderDTO> getOrder(
            @PathVariable
            @Parameter(description = "Unique identifier of the order") UUID orderId) {
        var founded = orderService.getOrderById(orderId);
        return ResponseEntity.ok(founded);
    }

    @GetMapping("/user/{customerId}")
    @Operation(summary = "Get orders by customer ID",
            description = "Retrieve a list of orders associated with a specific customer")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomerId(
            @PathVariable
            @Parameter(description = "Unique identifier of the customer") Long customerId) {
        var ordersList = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(ordersList);
    }

    @PostMapping
    @Operation(summary = "Create a new order",
            description = "Create a new order based on the provided order request details",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details of the order to be created",
                    required = true,
                    content = @Content(mediaType = "application/json")))
    public ResponseEntity<OrderDTO> createOrder(
            @RequestBody @Valid
            @Parameter(description = "Order request details") OrderRequest orderRequest) {
        var orderEntity = productReservationService.checkStockAndReserveProducts(orderRequest);
        var dto = orderService.createOrder(orderEntity);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{orderId}")
    @Operation(summary = "Update order status",
            description = "Update the status of an existing order",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New status for the order",
                    required = true,
                    content = @Content(mediaType = "application/json")))
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable
            @Parameter(description = "Unique identifier of the order") UUID orderId,
            @RequestBody
            @Parameter(description = "New status to update") OrderEntity.Status newStatus) {
        var updated = orderService.updateOrderStatus(orderId, newStatus);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "Delete an order",
            description = "Mark an order as deleted by its unique identifier")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable
            @Parameter(description = "Unique identifier of the order") UUID orderId) {
        if (orderService.deleteOrder(orderId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
