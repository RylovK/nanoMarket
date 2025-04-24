package my.project.cartservice.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@RedisHash("Cart")
public class Cart implements Serializable {

    @Id
    @NotNull(message = "Cart id cannot be empty")
    @Indexed
    private Long id;

    @NotNull(message = "Items map cannot be null")
    private Set<CartItem> items = new HashSet<>();

    private LocalDateTime lastUpdated = LocalDateTime.now();

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                '}';
    }
}
