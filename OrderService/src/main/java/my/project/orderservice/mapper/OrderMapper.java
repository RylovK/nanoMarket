package my.project.orderservice.mapper;

import my.project.orderservice.dto.OrderDTO;
import my.project.orderservice.dto.OrderItemDTO;
import my.project.orderservice.entity.Order;
import my.project.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "items", source = "items")
    OrderDTO toOrderDTO(Order order);

    @Mapping(target = "order", ignore = true)
    OrderItemDTO toOrderItemDTO(OrderItem orderItem);

    @Mapping(target = "items", source = "items")
    Order toOrderEntity(OrderDTO orderDTO);

    @Mapping(target = "order", ignore = true)
    OrderItem toOrderItemEntity(OrderItemDTO orderItemDTO);
}
