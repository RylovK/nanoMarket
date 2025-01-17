package my.project.productservice.controller;

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
public class StockController {

    private final ProductService productService;

    @GetMapping("/availability/{id}")
    public ResponseEntity<ProductAvailabilityDTO> getProductAvailability(@PathVariable Long id) {
        ProductAvailabilityDTO availability = productService.getProductAvailability(id);
        return ResponseEntity.ok(availability);
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveProducts(@RequestBody @Valid List<ProductReservationRequest> reservationRequests) {
        productService.reserveProducts(reservationRequests);
        return ResponseEntity.ok().build();
    }
}
