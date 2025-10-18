package ru.yandex.practicum.commerce.delivery.model;

import interaction.model.delivery.DeliveryState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Entity
@Table(name = "deliveries")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Delivery {

    @Id
    @Column(name = "delivery_id")
    private UUID deliveryId;

    @Column(name = "total_volume")
    private Double totalVolume;

    @Column(name = "total_weight")
    private Double totalWeight;

    @Column(name = "is_fragile")
    private Boolean isFragile;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "from_country")),
            @AttributeOverride(name = "city", column = @Column(name = "from_city")),
            @AttributeOverride(name = "street", column = @Column(name = "from_street")),
            @AttributeOverride(name = "house", column = @Column(name = "from_house")),
            @AttributeOverride(name = "flat", column = @Column(name = "from_flat"))
    })
    private Address fromAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "to_country")),
            @AttributeOverride(name = "city", column = @Column(name = "to_city")),
            @AttributeOverride(name = "street", column = @Column(name = "to_street")),
            @AttributeOverride(name = "house", column = @Column(name = "to_house")),
            @AttributeOverride(name = "flat", column = @Column(name = "to_flat"))
    })
    private Address toAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private DeliveryState state;

    @Column(name = "order_id")
    private UUID orderId;
}