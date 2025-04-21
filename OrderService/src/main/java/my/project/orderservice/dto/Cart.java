package my.project.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Cart {

    private Long id;

    private List<CartItem> items = new ArrayList<>();

    private LocalDateTime lastUpdated = LocalDateTime.now();
}
