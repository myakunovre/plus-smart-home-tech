package ru.yandex.practicum.commerce.payment.service;

import interaction.client.DeliveryFeignClient;
import interaction.client.OrderFeignClient;
import interaction.client.StoreFeignClient;
import interaction.model.order.OrderDto;
import interaction.model.payment.PaymentDto;
import interaction.model.store.dto.ProductDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.payment.mapper.PaymentMapper;
import ru.yandex.practicum.commerce.payment.model.Payment;
import ru.yandex.practicum.commerce.payment.model.PaymentStatus;
import ru.yandex.practicum.commerce.payment.repository.PaymentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final OrderFeignClient orderClient;
    private final StoreFeignClient storeClient;
    private final DeliveryFeignClient deliveryClient;

    @Transactional
    @Override
    public PaymentDto createPayment(OrderDto order) {

        orderClient.getById(order.getOrderId());

        Payment payment = new Payment(
                null,
                order.getOrderId(),
                order.getTotalPrice(),
                order.getDeliveryPrice(),
                PaymentStatus.PENDING
        );

        return mapper.toDto(repository.save(payment));
    }

    @Transactional
    @Override
    public Double calculateTotalCost(OrderDto order) {

        Double productPrice = calculateProductCost(order);
        Double productTax = calculateProductCost(order) * 0.1;
        Double deliveryPrice = deliveryClient.calculateDeliveryCost(order);

        return productPrice + productTax + deliveryPrice;
    }

    @Override
    public void simulateSuccessfulPayment(UUID paymentId) {
        Payment payment = getPayment(paymentId);
        payment.setStatus(PaymentStatus.SUCCESS);
        repository.save(payment);

        orderClient.paymentOrder(payment.getOrderId());
    }

    @Override
    public void simulateFailedPayment(UUID paymentId) {
        Payment payment = getPayment(paymentId);
        payment.setStatus(PaymentStatus.FAILED);
        repository.save(payment);

        orderClient.paymentFailedOrder(payment.getOrderId());
    }

    @Transactional
    @Override
    public Double calculateProductCost(OrderDto order) {
        Map<UUID, Long> productsInOrder = order.getProducts();
        List<UUID> productIds = new ArrayList<>(productsInOrder.keySet());

        List<ProductDto> productDetails = storeClient.getProductsByIds(productIds);

        Map<UUID, ProductDto> productLookup = productDetails.stream()
                .collect(Collectors.toMap(ProductDto::getProductId, Function.identity()));

        double resultProductCost = 0.0;
        for (Map.Entry<UUID, Long> entry : productsInOrder.entrySet()) {

            UUID productId = entry.getKey();
            ProductDto product = productLookup.get(productId);

            Long quantity = entry.getValue();
            resultProductCost = resultProductCost + product.getPrice() * quantity;
        }
        return resultProductCost;
    }

    private Payment getPayment(UUID paymentId) {
        return repository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment with id " + paymentId + " not found"));
    }
}