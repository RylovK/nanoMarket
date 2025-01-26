package my.project.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.productservice.dto.ProductAvailabilityDTO;
import my.project.productservice.dto.ProductReservationRequest;
import my.project.productservice.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
@Tag(name = "Stock API", description = "API for managing product stock and availability")
public class StockController {

    private final ProductService productService;

    @GetMapping("/availability/{id}")
    @Operation(summary = "Get product availability by ID",
            description = "Retrieve the availability details of a specific product by its ID")
    public ResponseEntity<ProductAvailabilityDTO> getProductAvailability(
            @PathVariable
            @Parameter(description = "ID of the product to check availability") Long id) {
        ProductAvailabilityDTO availability = productService.getProductAvailability(id);
        return ResponseEntity.ok(availability);
    }

    @PostMapping("/reserve")
    @Operation(summary = "Reserve products",
            description = "Reserve specified quantities of products to ensure their availability for an order",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "List of product reservation requests",
                    required = true,
                    content = @Content(mediaType = "application/json")
            ))
    public ResponseEntity<Void> reserveProducts(
            @RequestBody @Valid
            @Parameter(description = "List of products and their quantities to reserve", required = true)
            List<ProductReservationRequest> reservationRequests) {
        productService.reserveProducts(reservationRequests);
        return ResponseEntity.ok().build();
    }
}

