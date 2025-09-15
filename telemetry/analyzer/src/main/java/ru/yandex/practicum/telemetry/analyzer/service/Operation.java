package ru.yandex.practicum.telemetry.analyzer.service;


import ru.yandex.practicum.telemetry.analyzer.dal.entity.ConditionOperation;

public class Operation {
    public static boolean compareNumeric(int sensorValue, int baseValue, ConditionOperation operation) {
        return switch (operation) {
            case EQUALS -> sensorValue == baseValue;
            case GREATER_THAN -> sensorValue > baseValue;
            case LOWER_THAN -> sensorValue < baseValue;
        };
    }

    public static boolean compareBoolean(boolean sensorValue, boolean baseValue, ConditionOperation operation) {
        if (operation != ConditionOperation.EQUALS) {
            throw new IllegalArgumentException("Boolean values only support EQUALS operation, got: " + operation);
        }
        return sensorValue == baseValue;
    }
}