package ru.yandex.practicum.commerce.delivery.mapper;

import interaction.model.delivery.DeliveryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.commerce.delivery.model.Delivery;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeliveryMapper {

    @Mapping(source = "state", target = "deliveryState")
    DeliveryDto toDto(Delivery entity);

    @Mapping(source = "deliveryState", target = "state")
    Delivery toEntity(DeliveryDto dto);
}



