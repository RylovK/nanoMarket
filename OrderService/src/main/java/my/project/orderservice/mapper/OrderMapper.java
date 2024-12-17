package my.project.orderservice.mapper;

import my.project.orderservice.dto.OrderDTO;
import my.project.orderservice.dto.OrderItemDTO;
import my.project.orderservice.entity.Order;
import my.project.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

//    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "items", source = "items")
    OrderDTO toOrderDTO(Order order);

    @Mapping(target = "order", ignore = true) // Избегаем циклических зависимостей
    OrderItemDTO toOrderItemDTO(OrderItem orderItem);

    // Метод для обратного маппинга (DTO -> Entity), если это необходимо
    @Mapping(target = "items", source = "items")
    Order toOrderEntity(OrderDTO orderDTO);

    @Mapping(target = "order", ignore = true)
    OrderItem toOrderItemEntity(OrderItemDTO orderItemDTO);
}
