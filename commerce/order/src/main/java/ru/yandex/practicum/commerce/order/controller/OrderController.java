package ru.yandex.practicum.commerce.order.controller;

import interaction.client.OrderFeignClient;
import interaction.model.order.CreateNewOrderRequest;
import interaction.model.order.OrderDto;
import interaction.model.order.ProductReturnRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.order.service.OrderService;

import java.util.UUID;

@RequestMapping("/api/v1/order")
@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderController implements OrderFeignClient {

    private final OrderService service;

    @Override
    @GetMapping
    public Page<OrderDto> getOrders(@Valid @NotEmpty @RequestParam String username, Pageable pageable) {
        log.debug("Новый запрос на получение заказа от пользователя {}", username);
        return service.getOrders(username, pageable);
    }

    @Override
    @PutMapping
    public OrderDto addOrder(@Valid @RequestBody CreateNewOrderRequest request) {
        return service.addOrder(request);
    }

    @Override
    @PostMapping("/return")
    public OrderDto returnProducts(@Valid @RequestBody ProductReturnRequest returnProducts) {
        return service.returnProductsFromOrder(returnProducts);
    }

    @Override
    @PostMapping("/payment")
    public OrderDto paymentOrder(@RequestBody UUID orderId) {
        return service.paymentOrder(orderId);
    }

    @Override
    @PostMapping("/payment/failed")
    public OrderDto paymentFailedOrder(@RequestBody UUID orderId) {
        return service.paymentFailedOrder(orderId);
    }

    @Override
    @PostMapping("/delivery")
    public OrderDto deliveryOrder(@RequestBody UUID orderId) {
        return service.deliveryOrder(orderId);
    }

    @Override
    @PostMapping("/delivery/failed")
    public OrderDto deliveryFailedOrder(@RequestBody UUID orderId) {
        return service.deliveryFailedOrder(orderId);
    }

    @Override
    @PostMapping("/completed")
    public OrderDto completedOrder(@RequestBody UUID orderId) {
        return service.completedOrder(orderId);
    }

    @Override
    @PostMapping("/calculate/total")
    public OrderDto calculateTotalPayment(@RequestBody UUID orderId) {
        return service.calculateTotalPayment(orderId);
    }

    @Override
    @PostMapping("/calculate/delivery")
    public OrderDto calculateDelivery(@RequestBody UUID orderId) {
        return service.calculateDelivery(orderId);
    }

    @Override
    @PostMapping("/assembly")
    public OrderDto assemblyOrder(@RequestBody UUID orderId) {
        return service.assemblyOrder(orderId);
    }

    @Override
    @PostMapping("/assembly/failed")
    public OrderDto assemblyFailedOrder(@RequestBody UUID orderId) {
        return service.assemblyFailedOrder(orderId);
    }

    @Override
    @GetMapping("{orderId}")
    public OrderDto getById(@PathVariable UUID orderId) {
        log.debug("Новый запрос от микросервиса на получение заказа с id = {}", orderId);
        return service.getById(orderId);
    }
}