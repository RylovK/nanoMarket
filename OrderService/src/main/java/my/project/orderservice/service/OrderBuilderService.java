package my.project.orderservice.service;


import my.project.orderservice.entity.OrderEntity;

public interface OrderBuilderService {

    OrderEntity buildOrderFromCart(Long customerId);
}
