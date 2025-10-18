package ru.yandex.practicum.commerce.order.mapper;

import interaction.model.order.OrderDto;
import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.order.model.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDto toDto(Order order);

    Order toEntity(OrderDto orderDto);
}
