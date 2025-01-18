package my.project.orderservice.dto;
/**
 * Represents a request to create or process an order.
 * Contains the customer ID associated with the order.
 */
public record OrderRequest(Long customerId) {}
