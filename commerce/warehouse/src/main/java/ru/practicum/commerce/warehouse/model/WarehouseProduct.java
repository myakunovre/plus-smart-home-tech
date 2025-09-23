package ru.practicum.commerce.warehouse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseProduct {

    @Id
    private UUID productId;

    @Embedded
    private Dimension dimension;

    @Column
    private double weight;

    @Column
    private boolean fragile;

    @Column
    private long quantity;
}