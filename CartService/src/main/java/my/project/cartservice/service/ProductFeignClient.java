package my.project.cartservice.service;

import my.project.cartservice.dto.ProductAvailabilityDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("product-service")
public interface ProductFeignClient {

    @GetMapping("/api/v1/stocks/availability/{id}")
    ProductAvailabilityDTO getProductAvailability(@PathVariable("id") Long id);
}
