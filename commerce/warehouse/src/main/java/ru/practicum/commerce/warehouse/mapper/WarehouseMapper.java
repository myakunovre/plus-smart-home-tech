package ru.practicum.commerce.warehouse.mapper;

import interaction.model.warehouse.DimensionDto;
import org.mapstruct.Mapper;
import ru.practicum.commerce.warehouse.model.Dimension;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    Dimension dtoToDimension(DimensionDto dto);

    DimensionDto dimensionToDto(Dimension dimension);
}