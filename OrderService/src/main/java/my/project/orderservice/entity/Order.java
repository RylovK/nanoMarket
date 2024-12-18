package my.project.orderservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter @Setter
public class Order {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private Long customerId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @Embedded
    @Enumerated(EnumType.STRING)
    private Status status = Status.CREATED;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Embeddable
    @Getter
    public enum Status {
        CREATED,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        DELETED
    }
}
