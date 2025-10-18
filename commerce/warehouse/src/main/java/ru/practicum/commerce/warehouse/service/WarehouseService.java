package ru.practicum.commerce.warehouse.service;

import interaction.model.cart.ShoppingCartDto;
import interaction.model.warehouse.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void addNewProduct(NewProductInWarehouseRequest request);

    BookedProductDto bookProducts(ShoppingCartDto cart);

    void addQuantity(AddProductToWarehouseRequest request);

    AddressDto getCurrentAddress();

    BookedProductDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request);

    void shippedToDelivery(ShippedToDeliveryRequest request);

    void acceptReturn(Map<UUID, Long> productsToReturn);
}