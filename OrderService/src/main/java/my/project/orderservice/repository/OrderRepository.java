package my.project.orderservice.repository;

import my.project.orderservice.dto.OrderDTO;
import my.project.orderservice.entity.OrderEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends CrudRepository<OrderEntity, UUID> {

    List<OrderEntity> findByCustomerId(Long customerId);

}
