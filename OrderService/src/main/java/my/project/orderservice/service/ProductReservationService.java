package my.project.orderservice.service;

import my.project.orderservice.dto.OrderRequest;
import my.project.orderservice.entity.OrderEntity;

public interface ProductReservationService {

    OrderEntity checkStockAndReserveProducts(OrderRequest orderRequest);
}
