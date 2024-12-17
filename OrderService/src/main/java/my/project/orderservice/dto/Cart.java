package my.project.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Cart {

    private Long id;

    private Map<Long, Integer> items = new HashMap<>();

    private LocalDateTime lastUpdated = LocalDateTime.now();
}
