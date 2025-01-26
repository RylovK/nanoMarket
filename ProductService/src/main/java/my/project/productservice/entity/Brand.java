package my.project.productservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
public class Brand extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String brandName;

    private String logoUrl;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Brand brand)) return false;

        return getId().equals(brand.getId()) && getBrandName().equals(brand.getBrandName());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getBrandName().hashCode();
        return result;
    }
}
