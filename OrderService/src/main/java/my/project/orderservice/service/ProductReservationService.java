package my.project.orderservice.service;

import my.project.orderservice.dto.OrderRequest;
import my.project.orderservice.entity.OrderEntity;
/**
 * Service interface for handling product reservations.
 * Provides a method to check product stock availability and reserve the products
 * based on the details of an order request.
 */
public interface ProductReservationService {

    /**
     * Verifies product stock availability and reserves the products based on the provided order request.
     *
     * @param orderRequest the {@link OrderRequest} object containing the customer details and products to be reserved
     * @return an {@link OrderEntity} object representing the processed order, with reserved products
     */
    OrderEntity checkStockAndReserveProducts(OrderRequest orderRequest);
}
