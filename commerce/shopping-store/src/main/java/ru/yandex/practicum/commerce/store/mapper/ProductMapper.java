package ru.yandex.practicum.commerce.store.mapper;

import interaction.model.store.dto.ProductDto;
import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.store.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto productToProductDto(Product product);

    Product productDtoToProduct(ProductDto productDto);
}