package ru.yandex.practicum.commerce.store.repository;

import interaction.model.store.enums.ProductCategory;
import interaction.model.store.enums.ProductState;
import interaction.model.store.enums.QuantityState;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.commerce.store.model.Product;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findAllByProductCategory(ProductCategory productCategory, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Product p set p.productState = :state where p.productId = :productId")
    int updateProductState(UUID productId, ProductState state);

    @Transactional
    @Modifying
    @Query("update Product p set p.quantityState = :state where p.productId = :productId")
    int updateProductQuantityState(UUID productId, QuantityState state);

}