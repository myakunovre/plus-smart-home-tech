package ru.practicum.commerce.warehouse.service;

import interaction.model.cart.ShoppingCartDto;
import interaction.model.warehouse.AddProductToWarehouseRequest;
import interaction.model.warehouse.AddressDto;
import interaction.model.warehouse.BookedProductDto;
import interaction.model.warehouse.NewProductInWarehouseRequest;

public interface WarehouseService {
    void addNewProduct(NewProductInWarehouseRequest request);

    BookedProductDto bookProduct(ShoppingCartDto cart);

    void addQuantity(AddProductToWarehouseRequest request);

    AddressDto getCurrentAddress();
}