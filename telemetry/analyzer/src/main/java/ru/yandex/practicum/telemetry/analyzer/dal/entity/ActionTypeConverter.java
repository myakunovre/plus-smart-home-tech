package ru.yandex.practicum.telemetry.analyzer.dal.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ActionTypeConverter implements AttributeConverter<ActionType, String> {
    @Override
    public String convertToDatabaseColumn(ActionType action) {
        return action == null ? null : action.name().toLowerCase();
    }

    @Override
    public ActionType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ActionType.valueOf(dbData.toUpperCase());
    }
}