package my.project.orderservice.service;

import jakarta.validation.Valid;
import my.project.orderservice.dto.OrderDTO;
import my.project.orderservice.dto.OrderRequest;

public interface OrderService {

    OrderDTO createOrder(@Valid OrderRequest orderRequest);
}
