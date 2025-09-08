package ru.yandex.practicum.telemetry.analyzer.dal.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ConditionOperationConverter implements AttributeConverter<ConditionOperation, String> {
    @Override
    public String convertToDatabaseColumn(ConditionOperation condition) {
        return condition == null ? null : condition.name().toLowerCase();
    }

    @Override
    public ConditionOperation convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ConditionOperation.valueOf(dbData.toUpperCase());
    }
}