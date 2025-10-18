package ru.practicum.commerce.warehouse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "order_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderBooking {

    @Id
    private UUID id;

    @Column(name = "order_id")
    private UUID orderId;

    @ElementCollection
    @CollectionTable(name = "booked_product_quantities", joinColumns = @JoinColumn(name = "booking_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Long> bookedProducts;

    @Column(name = "delivery_id")
    private UUID deliveryId;
}