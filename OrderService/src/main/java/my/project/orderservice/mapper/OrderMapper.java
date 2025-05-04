package my.project.orderservice.mapper;

import my.project.orderservice.dto.CartItem;
import my.project.orderservice.dto.OrderDTO;
import my.project.orderservice.entity.OrderItem;
import my.project.events.OrderCreatedEvent;
import my.project.orderservice.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "items", source = "items")
    OrderDTO toOrderDTO(OrderEntity orderEntity);

    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "items", source = "items")
    OrderCreatedEvent toOrderCreatedEvent(OrderEntity orderEntity);

    OrderItem cartItemToOrderItem(CartItem cartItem);

//    @Mapping(target = "order", ignore = true)
//    OrderItemDTO toOrderItemDTO(OrderItem orderItem);
//
//    @Mapping(target = "items", source = "items")
//    OrderEntity toOrderEntity(OrderDTO orderDTO);
//
//    OrderItem toOrderItemEntity(OrderItemDTO orderItemDTO);
}
