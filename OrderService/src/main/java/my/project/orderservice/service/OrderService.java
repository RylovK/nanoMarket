package my.project.orderservice.service;

import jakarta.validation.Valid;
import my.project.orderservice.dto.OrderDTO;
import my.project.orderservice.entity.OrderEntity;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing orders in the Order Service.
 * Provides methods for creating, retrieving, updating, and deleting orders.
 */
public interface OrderService {

    /**
     * Creates a new order.
     *
     * @param orderEntity the {@link OrderEntity} object containing the details of the order to be created
     * @return the created {@link OrderDTO} object representing the order
     */
    OrderDTO createOrder(@Valid OrderEntity orderEntity);

    /**
     * Retrieves an order by its unique identifier.
     *
     * @param orderId the unique identifier of the order to retrieve
     * @return the {@link OrderDTO} object representing the order with the given ID
     */
    OrderDTO getOrderById(UUID orderId);

    /**
     * Retrieves a list of orders for a specific customer.
     *
     * @param userId the unique identifier of the customer whose orders to retrieve
     * @return a list of {@link OrderDTO} objects representing the orders placed by the given customer
     */
    List<OrderDTO> getOrdersByCustomerId(Long userId);

    /**
     * Updates the status of an existing order.
     *
     * @param orderId the unique identifier of the order to update
     * @param newStatus the new status to set for the order
     * @return the updated {@link OrderDTO} object representing the order with the updated status
     */
    OrderDTO updateOrderStatus(UUID orderId, OrderEntity.Status newStatus);

    /**
     * Deletes an order by its unique identifier.
     *
     * @param orderId the unique identifier of the order to delete
     * @return {@code true} if the order was successfully deleted, {@code false} otherwise
     */
    boolean deleteOrder(UUID orderId);
}