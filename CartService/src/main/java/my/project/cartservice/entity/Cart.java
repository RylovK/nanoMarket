package my.project.cartservice.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@RedisHash("Cart")
public class Cart implements Serializable {

    @Id
    @NotBlank(message = "Cart id cannot be empty")
    @Indexed
    private Long id;

    @NotNull(message = "Items map cannot be null")
    private Map<Long, @Min(1) Integer> items = new HashMap<>();

    private LocalDateTime lastUpdated = LocalDateTime.now();

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                '}';
    }
}
