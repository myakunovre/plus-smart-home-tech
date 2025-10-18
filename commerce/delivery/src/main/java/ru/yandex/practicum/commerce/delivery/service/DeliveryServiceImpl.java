package ru.yandex.practicum.commerce.delivery.service;

import interaction.client.OrderFeignClient;
import interaction.client.StoreFeignClient;
import interaction.client.WarehouseFeignClient;
import interaction.model.delivery.DeliveryDto;
import interaction.model.delivery.DeliveryState;
import interaction.model.order.OrderDto;
import interaction.model.warehouse.ShippedToDeliveryRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.commerce.delivery.model.Address;
import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.delivery.repository.DeliveryRepository;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository repository;
    private final DeliveryMapper mapper;
    private final OrderFeignClient orderClient;
    private final StoreFeignClient storeClient;
    private final WarehouseFeignClient warehouseClient;

    static final String ADDRESS_1 = "ADDRESS_1";
    static final String ADDRESS_2 = "ADDRESS_2";


    @Override
    public DeliveryDto addDelivery(DeliveryDto delivery) {
        Delivery deliveryToSave = mapper.toEntity(delivery);
        return mapper.toDto(repository.save(deliveryToSave));
        // нет метода для создания доставки в сервисе order
    }

    @Transactional
    @Override
    public void simulateSuccessfulDelivery(UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setState(DeliveryState.DELIVERED);
        repository.save(delivery);

        orderClient.deliveryOrder(orderId);
    }

    @Transactional
    @Override
    public void simulateDeliveryReceived(UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setState(DeliveryState.IN_PROGRESS);
        repository.save(delivery);

        orderClient.assemblyOrder(orderId);
        warehouseClient.shippedToDelivery(new ShippedToDeliveryRequest(orderId, delivery.getDeliveryId()));
    }

    @Transactional
    @Override
    public void simulateDeliveryFailed(UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setState(DeliveryState.FAILED);
        repository.save(delivery);

        orderClient.deliveryFailedOrder(orderId);
    }

    @Transactional
    @Override
    public Double calculateDeliveryCost(OrderDto order) {
        Delivery delivery = repository.findById(order.getDeliveryId())
                .orElseThrow(() -> new IllegalArgumentException("Delivery with id " + order.getDeliveryId() + " not found"));

        double currentCost = 5.0;

        // 1. Учет адреса склада (ADDRESS_1 / ADDRESS_2)
        Address warehouseAddress = delivery.getFromAddress();
        double addressMultiplier = 0.0;

        boolean containsKeyword1 = containsKeyword(warehouseAddress, ADDRESS_1);
        boolean containsKeyword2 = containsKeyword(warehouseAddress, ADDRESS_2);

        if (containsKeyword1 && !containsKeyword2) { // Только ADDRESS_1
            addressMultiplier = 1.0;
        } else if (containsKeyword2 && !containsKeyword1) { // Только ADDRESS_2
            addressMultiplier = 2.0;
        } else if (containsKeyword1 && containsKeyword2) {
            addressMultiplier = 2.0; // Если адрес содержит оба ключевых слова
        } else {
            addressMultiplier = 1.0; // Если адрес не содержит ни ADDRESS_1_KEYWORD, ни ADDRESS_2_KEYWORD
        }

        currentCost = currentCost + (5.0 * addressMultiplier);

        // 2. Если в заказе есть признак хрупкости
        if (delivery.getIsFragile() != null && delivery.getIsFragile()) {
            currentCost = currentCost + currentCost * 0.2;
        }

        // 3. Добавляем к сумме вес заказа, умноженный на 0.3
        if (delivery.getTotalWeight() != null) {
            currentCost = currentCost + delivery.getTotalWeight() * 0.3;
        }

        // 4. Складываем с итогом объем, умноженный на 0.2
        currentCost = currentCost + delivery.getTotalVolume() * 0.2;

        // 5. Учет адреса доставки (сравнение улиц)
        boolean sameStreet = false;
        if (delivery.getFromAddress() != null && delivery.getToAddress() != null) {
            sameStreet = Objects.equals(delivery.getFromAddress().getStreet(), delivery.getToAddress().getStreet());
        }

        if (!sameStreet) {
            currentCost = currentCost + currentCost * 0.2;
        }

        // 6. Возвращаем результат
        return currentCost;
    }

    private boolean containsKeyword(Address address, String keyword) {
        if (address == null || keyword == null) {
            return false;
        }
        String lowerKeyword = keyword.toLowerCase();
        return (address.getCountry() != null && address.getCountry().toLowerCase().contains(lowerKeyword)) ||
                (address.getCity() != null && address.getCity().toLowerCase().contains(lowerKeyword)) ||
                (address.getStreet() != null && address.getStreet().toLowerCase().contains(lowerKeyword)) ||
                (address.getHouse() != null && address.getHouse().toLowerCase().contains(lowerKeyword)) ||
                (address.getFlat() != null && address.getFlat().toLowerCase().contains(lowerKeyword));
    }

    private Delivery getDeliveryByOrderId(UUID orderId) {
        UUID deliveryId = orderClient.getById(orderId).getDeliveryId();
        return repository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery with id " + deliveryId + " not found"));
    }
}