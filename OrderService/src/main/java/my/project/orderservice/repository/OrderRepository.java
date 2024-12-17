package my.project.orderservice.repository;

import my.project.orderservice.entity.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends CrudRepository<Order, UUID> {

    List<Order> findByCustomerId(Long customerId);
}
