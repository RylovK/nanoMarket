package my.project.orderservice.service;

import my.project.orderservice.dto.Cart;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cart-service")
public interface CartFeignClient {

    @GetMapping("/api/v1/carts/{cartId}")
    Cart getCart(@PathVariable("cartId") Long cartId);

    @DeleteMapping("/api/v1/carts/{cartId}")
    void clearCart(@PathVariable("cartId") Long cartId);
}
