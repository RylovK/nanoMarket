package my.project.orderservice.service;


import my.project.orderservice.dto.ProductAvailabilityDTO;
import my.project.orderservice.dto.ProductReservationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * Feign client for interacting with the Product Service.
 * This client provides methods to check product availability and reserve products.
 */
@FeignClient("product-service")
public interface ProductFeignClient {

    /**
     * Retrieves the availability of a product by its unique identifier.
     *
     * @param id the unique identifier of the product whose availability to check
     * @return a {@link ProductAvailabilityDTO} object representing the availability of the product
     */
    @GetMapping("/api/v1/stocks/availability/{id}")
    ProductAvailabilityDTO getProductAvailability(@PathVariable("id") Long id);

    /**
     * Reserves a list of products.
     *
     * @param products a list of {@link ProductReservationRequest} objects representing the products to reserve
     */
    @PostMapping("/api/v1/stocks/reserve")
    void reserveProducts(List<ProductReservationRequest> products);
}
