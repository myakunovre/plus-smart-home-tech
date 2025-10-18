package ru.yandex.practicum.commerce.payment.mapper;

import interaction.model.payment.PaymentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.commerce.payment.model.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    double TAX_RATE = 0.10; // 10%

    @Mapping(target = "feeTotal", expression = "java(payment.getTotalPayment() * TAX_RATE)")
    PaymentDto toDto(Payment payment);

    Payment toEntity(PaymentDto dto);
}