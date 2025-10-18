package ru.practicum.commerce.warehouse.service;

import interaction.model.cart.ShoppingCartDto;
import interaction.model.warehouse.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.commerce.warehouse.mapper.WarehouseMapper;
import ru.practicum.commerce.warehouse.model.OrderBooking;
import ru.practicum.commerce.warehouse.model.WarehouseProduct;
import ru.practicum.commerce.warehouse.repository.OrderBookingRepository;
import ru.practicum.commerce.warehouse.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};
    private final static String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];
    private final WarehouseRepository warehouseRepository;
    private final OrderBookingRepository orderBookingRepository;
    private final WarehouseMapper mapper;

    @Override
    public void addNewProduct(NewProductInWarehouseRequest request){
        if(request.getProductId() != null && warehouseRepository.existsById(request.getProductId())){
            throw new IllegalArgumentException("Product already exists");
        }

        WarehouseProduct product = new WarehouseProduct();
        product.setProductId(request.getProductId());
        product.setFragile(request.isFragile());
        product.setDimension(mapper.dtoToDimension(request.getDimension()));
        product.setWeight(request.getWeight());
        product.setQuantity(0L);
        warehouseRepository.save(product);
    }

    @Override
    public BookedProductDto bookProducts(ShoppingCartDto cart){
        Map<UUID, Long> productsToBooking = cart.getProducts();

        // 1. Извлечь товары из базы данных склада
        List<WarehouseProduct> productsInWarehouse = getWarehouseProducts(productsToBooking);

        // 2. Проверить наличие товара на складе и создать BookedProductDto
        Map<UUID, Long> bookedProducts = new HashMap<>();

        double totalVolume = 0;
        double totalWeight = 0;
        boolean fragile = false;

        for (WarehouseProduct product : productsInWarehouse) {
            bookedProducts.put(product.getProductId(),
                    product.getQuantity() - productsToBooking.get(product.getProductId()));

            if (product.isFragile()) {
                fragile = true;
            }
            totalWeight = totalWeight + product.getWeight();
            totalVolume = totalVolume + (product.getDimension().getDepth()
                    * product.getDimension().getWidth()
                    * product.getDimension().getHeight()
            );
        }

        List<UUID> notEnoughProducts = bookedProducts.entrySet().stream()
                .filter((entry) -> entry.getValue() < 0)
                .map(Map.Entry::getKey)
                .toList();

        if (!notEnoughProducts.isEmpty()) {
            throw new IllegalArgumentException("Not enough products: \n " + notEnoughProducts);
        }
        return new BookedProductDto(totalWeight, totalVolume, fragile);
    }

    @Override
    @Transactional
    public BookedProductDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        Map<UUID, Long> productsToBooking = request.getProducts();

        // 1. Извлечь продукты из базы данных
        List<WarehouseProduct> productsInWarehouse = getWarehouseProducts(productsToBooking);

        // 2. Проверить наличие и уменьшить остатки
        Map<UUID, Long> bookedProductsInOrder = new HashMap<>();

        double totalVolume = 0;
        double totalWeight = 0;
        boolean fragile = false;

        for (WarehouseProduct warehouseProduct : productsInWarehouse) {
            Long requestedQuantity = productsToBooking.get(warehouseProduct.getProductId());
            if (requestedQuantity == null || requestedQuantity <= 0) {
                throw new IllegalArgumentException("Invalid quantity for product " + warehouseProduct.getProductId());
            }

            if (warehouseProduct.getQuantity() < requestedQuantity) {
                throw new IllegalArgumentException("Not enough quantity for product " + warehouseProduct.getProductId());
            }

            bookedProductsInOrder.put(warehouseProduct.getProductId(), requestedQuantity);

            if (warehouseProduct.isFragile()) {
                fragile = true;
            }
            totalWeight = totalWeight + warehouseProduct.getWeight();
            totalVolume = totalVolume + (warehouseProduct.getDimension().getDepth()
                    * warehouseProduct.getDimension().getWidth()
                    * warehouseProduct.getDimension().getHeight()
            );

            warehouseProduct.setQuantity(warehouseProduct.getQuantity() - requestedQuantity);
        }
        BookedProductDto bookedProductDto = new BookedProductDto(totalWeight, totalVolume, fragile);
        warehouseRepository.saveAll(productsInWarehouse);

        // 3. Создать сущность «Забронированные для заказа товары» (OrderBooking)
        OrderBooking orderBooking = new OrderBooking(
                null,
                request.getOrderId(),
                bookedProductsInOrder,
                null
        );
        orderBookingRepository.save(orderBooking);

        return bookedProductDto;
    }

    private List<WarehouseProduct> getWarehouseProducts(Map<UUID, Long> productsToBooking) {
        List<UUID> productIds = productsToBooking.keySet().stream().toList();

        List<WarehouseProduct> productsInWarehouse = warehouseRepository.findAllByProductIdIn(productIds);
        if (productsInWarehouse.size() != productsToBooking.size()) {
            List<UUID> foundedProducts = productsInWarehouse.stream()
                    .map(WarehouseProduct::getProductId)
                    .toList();
            List<UUID> notFoundedProducts = productIds.stream()
                    .filter(id -> !foundedProducts.contains(id))
                    .toList();
            throw new IllegalArgumentException("Not founded products: \n " + notFoundedProducts);
        }
        return productsInWarehouse;
    }

    @Override
    public void addQuantity(AddProductToWarehouseRequest request){
        WarehouseProduct product = warehouseRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product with id " + request.getProductId() + " not found"));
        product.setQuantity(product.getQuantity() + request.getQuantity());
        warehouseRepository.save(product);
    }

    @Override
    public AddressDto getCurrentAddress(){
        return new AddressDto(
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS);
    }

    @Override
    @Transactional
    public void shippedToDelivery(ShippedToDeliveryRequest request) {

        OrderBooking orderBooking = orderBookingRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order booking not found for order ID: " + request.getOrderId()));

        orderBooking.setDeliveryId(request.getDeliveryId());
        orderBookingRepository.save(orderBooking);
    }

    @Override
    @Transactional
    public void acceptReturn(Map<UUID, Long> productsToReturn) {
        List<WarehouseProduct> productsInWarehouse = getWarehouseProducts(productsToReturn);

        for (WarehouseProduct product : productsInWarehouse) {
            Long returnedQuantity = productsToReturn.get(product.getProductId());
            if (returnedQuantity == null || returnedQuantity <= 0) {
                throw new IllegalArgumentException("Invalid return quantity for product " + product.getProductId());
            }
            product.setQuantity(product.getQuantity() + returnedQuantity);
        }
        warehouseRepository.saveAll(productsInWarehouse);
    }
}