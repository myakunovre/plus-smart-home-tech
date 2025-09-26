package ru.yandex.practicum.commerce.cart.service;

import interaction.client.WarehouseFeignClient;
import interaction.model.cart.ChangeProductQuantityRequest;
import interaction.model.cart.ShoppingCartDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.cart.model.ShoppingCart;
import ru.yandex.practicum.commerce.cart.model.ShoppingCartState;
import ru.yandex.practicum.commerce.cart.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository repository;
    private final WarehouseFeignClient warehouseClient;

    @Override
    public ShoppingCartDto getShoppingCart(String name) {
        ShoppingCart cart = findExistingCart(name);
        return new ShoppingCartDto(cart.getShoppingCartId(), cart.getProducts());
    }

    @Transactional
    @Override
    public ShoppingCartDto addProductToCart(String name, Map<UUID, Long> addingProducts) {
        ShoppingCart cart = findExistingCart(name);
        if (cart.getState() == ShoppingCartState.DEACTIVATED) {
            throw new IllegalStateException("Cart has been deactivated");
        }

        Map<UUID, Long> productsInCart = cart.getProducts();
        addingProducts.forEach(
                (id, quantity) -> productsInCart.merge(id, quantity, Long::sum)
        );

        warehouseClient.checkAvailability(new ShoppingCartDto(cart.getShoppingCartId(), addingProducts));

        repository.save(cart);
        return new ShoppingCartDto(cart.getShoppingCartId(), productsInCart);
    }

    @Transactional
    @Override
    public ShoppingCartDto removeProductFromCart(String name, List<UUID> removingProducts) {
        ShoppingCart cart = findExistingCart(name);
        if (cart.getState() == ShoppingCartState.DEACTIVATED) {
            throw new IllegalStateException("Cart has been deactivated");
        }
        removingProducts.forEach(cart.getProducts()::remove);
        repository.save(cart);
        return new ShoppingCartDto(cart.getShoppingCartId(), cart.getProducts());
    }

    @Transactional
    @Override
    public ShoppingCartDto changeProductQuantity(String name, ChangeProductQuantityRequest request) {
        ShoppingCart cart = findExistingCart(name);
        if (cart.getState() == ShoppingCartState.DEACTIVATED) {
            throw new IllegalStateException("Cart has been deactivated");
        }
        if (!cart.getProducts().containsKey(request.getProductId())) {
            throw new IllegalArgumentException("Product not found");
        }
        cart.getProducts().put(request.getProductId(), request.getNewQuantity());
        repository.save(cart);
        return new ShoppingCartDto(cart.getShoppingCartId(), cart.getProducts());
    }

    @Transactional
    @Override
    public void deactivateShoppingCart(String name) {
        ShoppingCart cart = findExistingCart(name);
        if (cart.getState() == ShoppingCartState.DEACTIVATED) {
            return;
        }
        cart.setState(ShoppingCartState.DEACTIVATED);
        repository.save(cart);
    }

    private ShoppingCart findExistingCart(String username) {
        return repository.findByUsername(username).orElseGet(
                () -> repository.save(createNewCart(username))
        );
    }

    private ShoppingCart createNewCart(String username) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUsername(username);
        cart.setShoppingCartId(UUID.randomUUID());
        cart.setProducts(new HashMap<>());
        cart.setState(ShoppingCartState.ACTIVE);
        return cart;
    }
}