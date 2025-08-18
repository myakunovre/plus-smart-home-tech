package ru.yandex.practicum.telemetry.collector.utils;

import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.model.hub.*;

import java.util.List;
import java.util.stream.Collectors;

public class ScenarioMapper {

    // Маппер для ConditionType (enum)
    public static ConditionTypeAvro map(ConditionType type) {
        if (type == null) return null;
        return ConditionTypeAvro.valueOf(type.name());
    }

    // Маппер для ConditionOperation (enum)
    public static ConditionOperationAvro map(ConditionOperation operation) {
        if (operation == null) return null;
        return ConditionOperationAvro.valueOf(operation.name());
    }

    // Маппер для ActionType (enum)
    public static ActionTypeAvro map(ActionType type) {
        if (type == null) return null;
        return ActionTypeAvro.valueOf(type.name());
    }

    // Маппер для ScenarioCondition
    public static ScenarioConditionAvro map(ScenarioCondition condition) {
        if (condition == null) return null;
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(map(condition.getType()))
                .setOperation(map(condition.getOperation()))
                .setValue(condition.getValue())
                .build();
    }

    // Маппер для DeviceAction
    public static DeviceActionAvro map(DeviceAction action) {
        if (action == null) return null;
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(map(action.getType()))
                .setValue(action.getValue())
                .build();
    }

    // Маппер для списка ScenarioCondition
    public static List<ScenarioConditionAvro> mapConditions(List<ScenarioCondition> conditions) {
        if (conditions == null) {
            return null;
        }
        return conditions.stream()
                .map(ScenarioMapper::map)
                .collect(Collectors.toList());
    }

    // Маппер для списка DeviceAction
    public static List<DeviceActionAvro> mapActions(List<DeviceAction> actions) {
        if (actions == null) {
            return null;
        }
        return actions.stream()
                .map(ScenarioMapper::map)
                .collect(Collectors.toList());
    }
}