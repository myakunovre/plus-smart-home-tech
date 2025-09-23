package ru.practicum.commerce.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.commerce.warehouse.model.WarehouseProduct;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<WarehouseProduct, UUID> {
    List<WarehouseProduct> findAllByProductIdIn(Collection<UUID> productIds);
}