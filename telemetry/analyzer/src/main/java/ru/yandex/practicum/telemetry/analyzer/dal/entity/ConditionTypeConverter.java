package ru.yandex.practicum.telemetry.analyzer.dal.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ConditionTypeConverter implements AttributeConverter<ConditionType, String> {
    @Override
    public String convertToDatabaseColumn(ConditionType condition) {
        return condition == null ? null : condition.name().toLowerCase();
    }

    @Override
    public ConditionType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ConditionType.valueOf(dbData.toUpperCase());
    }
}