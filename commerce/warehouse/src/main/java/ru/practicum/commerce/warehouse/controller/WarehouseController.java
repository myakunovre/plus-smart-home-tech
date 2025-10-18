package ru.practicum.commerce.warehouse.controller;

import interaction.client.WarehouseFeignClient;
import interaction.model.cart.ShoppingCartDto;
import interaction.model.warehouse.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.commerce.warehouse.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseFeignClient {
    private final WarehouseService service;

    @Override
    @PutMapping
    public void registerNewProduct(@RequestBody NewProductInWarehouseRequest request) {
        service.addNewProduct(request);
    }

    @Override
    @PostMapping("/check")
    public BookedProductDto checkAvailability(ShoppingCartDto cart) {
        log.debug("Проверка достаточного количества товаров для корзины {}", cart.getShoppingCartId());
        return service.bookProducts(cart);
    }

    @Override
    @PostMapping("/add")
    public void addProductQuantity(AddProductToWarehouseRequest request) {
        service.addQuantity(request);
    }

    @Override
    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        return service.getCurrentAddress();
    }

    @Override
    @PostMapping("/assembly")
    public BookedProductDto assemblyProductsForOrder(
            @Valid @RequestBody AssemblyProductsForOrderRequest request) {
        log.info("Запрос на сборку заказа {} с товарами: {}", request.getOrderId(), request.getProducts());
        return service.assemblyProductsForOrder(request);
    }

    @Override
    @PostMapping("/shipped")
    public void shippedToDelivery(@Valid @RequestBody ShippedToDeliveryRequest request) {
        log.debug("Передача заказа {} в доставку {}", request.getOrderId(), request.getDeliveryId());
        service.shippedToDelivery(request);
    }

    @Override
    @PostMapping("/return")
    public void acceptReturn(@RequestBody Map<UUID, Long> productsToReturn) {
        log.debug("Возврат товаров {}", productsToReturn.keySet());
        service.acceptReturn(productsToReturn);
    }
}