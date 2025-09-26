package ru.yandex.practicum.commerce.cart.service;


import interaction.model.cart.ChangeProductQuantityRequest;
import interaction.model.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart(String username);

    ShoppingCartDto addProductToCart(String username, Map<UUID, Long> productsToAdd);

    void deactivateShoppingCart(String username);

    ShoppingCartDto removeProductFromCart(String username, List<UUID> productIds);

    ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request);
}
