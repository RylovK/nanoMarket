package my.project.productservice.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedEvent extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String messageId;

    @Column(nullable = false)
    private String orderId;
}
