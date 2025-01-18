package my.project.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a shopping cart, containing a unique identifier,
 * a map of product items (with their quantities), and the last updated timestamp.
 */
@Getter
@Setter
public class Cart {

    private Long id;

    private Map<Long, Integer> items = new HashMap<>();

    private LocalDateTime lastUpdated = LocalDateTime.now();
}
