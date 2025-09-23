package ru.yandex.practicum.commerce.store.service;

import interaction.model.store.dto.ProductDto;
import interaction.model.store.enums.ProductCategory;
import interaction.model.store.enums.ProductState;
import interaction.model.store.enums.QuantityState;
import interaction.model.store.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.store.mapper.ProductMapper;
import ru.yandex.practicum.commerce.store.model.Product;
import ru.yandex.practicum.commerce.store.repository.ProductRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public Page<ProductDto> getProductsByCategory(ProductCategory category, Pageable pageable) {
        return repository.findAllByProductCategory(category, pageable)
                .map(mapper::productToProductDto);
    }

    @Override
    public ProductDto getProductById(UUID id) {
        return repository.findById(id).map(mapper::productToProductDto)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        if (productDto.getProductId() == null) {
            productDto.setProductId(UUID.randomUUID());
        }
        Product product = mapper.productDtoToProduct(productDto);
        return mapper.productToProductDto(repository.save(product));
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        if (productDto.getProductId() == null || !repository.existsById(productDto.getProductId())) {
            throw new ProductNotFoundException("Product with id " + productDto.getProductId() + " not found");
        }

        Product product = mapper.productDtoToProduct(productDto);
        return mapper.productToProductDto(repository.save(product));
    }
    @Override
    public boolean deleteProductById(UUID id) {
        return repository.updateProductState(id, ProductState.DEACTIVATE) > 0;
    }

    @Override
    public boolean updateQuantity(UUID uuid, QuantityState state) {
        return repository.updateProductQuantityState(uuid, state) > 0;
    }
}