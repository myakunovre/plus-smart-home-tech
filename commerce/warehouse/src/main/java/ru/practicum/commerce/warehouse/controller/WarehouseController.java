package ru.practicum.commerce.warehouse.controller;

import interaction.client.WarehouseFeignClient;
import interaction.model.cart.ShoppingCartDto;
import interaction.model.warehouse.AddProductToWarehouseRequest;
import interaction.model.warehouse.AddressDto;
import interaction.model.warehouse.BookedProductDto;
import interaction.model.warehouse.NewProductInWarehouseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.commerce.warehouse.service.WarehouseService;

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
        return service.bookProduct(cart);
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
}