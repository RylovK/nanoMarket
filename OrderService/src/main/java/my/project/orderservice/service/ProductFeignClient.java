package my.project.orderservice.service;


import my.project.orderservice.dto.ProductAvailabilityDTO;
import my.project.orderservice.dto.ProductReservationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient("product-service")
public interface ProductFeignClient {

    @GetMapping("/api/v1/stock/availability/{id}")
    ProductAvailabilityDTO getProductAvailability(@PathVariable("id") Long id);

    @PostMapping
    void reserveProducts(List<ProductReservationRequest> products);
}
