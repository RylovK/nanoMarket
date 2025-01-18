package my.project.cartservice.service;

import my.project.cartservice.dto.ProductAvailabilityDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
/**
 * Feign client for interacting with the Product Service.
 * Provides methods to retrieve product availability information.
 */
@FeignClient("product-service")
public interface ProductFeignClient {

    /**
     * Retrieves the availability details of a product by its ID.
     *
     * @param id the unique identifier of the product
     * @return a {@link ProductAvailabilityDTO} containing the product's availability information
     */
    @GetMapping("/api/v1/stocks/availability/{id}")
    ProductAvailabilityDTO getProductAvailability(@PathVariable("id") Long id);
}
