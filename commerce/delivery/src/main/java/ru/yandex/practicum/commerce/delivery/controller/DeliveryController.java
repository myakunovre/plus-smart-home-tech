package ru.yandex.practicum.commerce.delivery.controller;

import interaction.client.DeliveryFeignClient;
import interaction.model.delivery.DeliveryDto;
import interaction.model.order.OrderDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.delivery.service.DeliveryService;

import java.util.UUID;

@RequestMapping("/api/v1/delivery")
@RestController
@RequiredArgsConstructor
@Slf4j
public class DeliveryController implements DeliveryFeignClient {

    private final DeliveryService service;

    @Override
    @PutMapping
    public DeliveryDto addDelivery(@Valid @RequestBody DeliveryDto delivery) {
        log.debug("Новый запрос на добавление доставки");
        return service.addDelivery(delivery);
    }

    @Override
    @PostMapping("/successful")
    public void simulateSuccessfulDelivery(@RequestBody UUID orderId) {
        log.debug("Новый запрос - эмуляция успешной доставки товара.");
        service.simulateSuccessfulDelivery(orderId);
    }

    @Override
    @PostMapping("/picked")
    public void simulateDeliveryReceived(@RequestBody UUID orderId) {
        log.debug("Новый запрос - эмуляция получения товара в доставку.");
        service.simulateDeliveryReceived(orderId);
    }

    @Override
    @PostMapping("/failed")
    public void simulateDeliveryFailed(@RequestBody UUID orderId) {
        log.debug("Новый запрос - Эмуляция неудачного вручения товара.");
        service.simulateDeliveryFailed(orderId);
    }

    @Override
    @PostMapping("/cost")
    public Double calculateDeliveryCost(@Valid @RequestBody OrderDto order) {
        log.debug("Новый запрос на расчёт полной стоимости доставки заказа.");
        return service.calculateDeliveryCost(order);
    }
}