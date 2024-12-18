package my.project.orderservice.service;

import my.project.orderservice.dto.OrderRequest;
import my.project.orderservice.entity.Order;

public interface ProductReservationService {

    Order checkStockAndReserveProducts(OrderRequest orderRequest);
}
