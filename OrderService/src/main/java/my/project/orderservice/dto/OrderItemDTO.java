package my.project.orderservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * A Data Transfer Object (DTO) representing an item in an order.
 * This record encapsulates the details of a specific product in an order,
 * including its unique identifier, product ID, price, and quantity.
 *
 * @param itemId    the unique identifier for the order item
 * @param productId the unique identifier of the product associated with this order item
 * @param price     the price of the product at the time of the order
 * @param quantity  the quantity of the product in the order
 */
public record OrderItemDTO (UUID itemId, Long productId, BigDecimal price, Integer quantity) {}
