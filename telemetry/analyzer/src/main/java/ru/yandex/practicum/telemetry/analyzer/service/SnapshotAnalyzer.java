package ru.yandex.practicum.telemetry.analyzer.service;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.dal.entity.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SnapshotAnalyzer {

    public List<DeviceActionRequest> analyze(SensorsSnapshotAvro snapshot, List<Scenario> scenarios) {
        String hubId = snapshot.getHubId();
        log.info("Analyzing snapshot for hub {}, with {} scenarios", hubId, scenarios.size());

        List<DeviceActionRequest> actionsRequest = new ArrayList<>();

        for (Scenario scenario : scenarios) {
            List<DeviceActionRequest> scenarioActions = analyzeScenario(scenario, snapshot);
            actionsRequest.addAll(scenarioActions);
        }
        log.info("Analysis complete for hub {}: {} actions identified", hubId, actionsRequest.size());
        return actionsRequest;
    }

    private List<DeviceActionRequest> analyzeScenario(Scenario scenario, SensorsSnapshotAvro snapshot) {
        List<DeviceActionRequest> scenarioActions = new ArrayList<>();
        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();
        Map<String, Condition> conditions = scenario.getConditions();
        Map<String, Action> actions = scenario.getActions();

        boolean allConditionsMet = true;

        for (Map.Entry<String, Condition> entry : conditions.entrySet()) {
            String sensorId = entry.getKey();
            SensorStateAvro state = sensorsState.get(sensorId);
            if (state == null) {
                log.info("Sensor with id '{}' not found in snapshot", sensorId);
                allConditionsMet = false;
                break;
            }
            Condition condition = entry.getValue();
            if (!checkCondition(condition, state)) {
                log.info("Condition '{}' not met for sensor '{}'", condition, sensorId);
                allConditionsMet = false;
                break;
            }
        }

        if (allConditionsMet) {
            log.info("All conditions met for scenario '{}', adding {} actions", scenario.getName(), actions.size());

            for (Map.Entry<String, Action> entry : actions.entrySet()) {
                String sensorId = entry.getKey();
                Action action = entry.getValue();
                SensorStateAvro state = sensorsState.get(sensorId);
                Instant timestamp = state != null ?
                        state.getTimestamp() : Instant.now();
                DeviceActionRequest request = createActionRequest(scenario, action, sensorId, timestamp);
                scenarioActions.add(request);
            }
        }
        return scenarioActions;
    }

    private boolean checkCondition(Condition condition,
                                   SensorStateAvro sensorState) {
        ConditionType type = condition.getType();
        ConditionOperation operation = condition.getOperation();
        int baseValue = condition.getValue();

        boolean result = switch (type) {
            case SWITCH, MOTION -> {
                boolean sensorValue = extractBooleanValue(sensorState, type);
                boolean value = baseValue != 0;
                yield Operation.compareBoolean(sensorValue, value, operation);
            }
            case TEMPERATURE, HUMIDITY, CO2LEVEL, LUMINOSITY -> {
                int sensorValue = extractNumericValue(sensorState, type);
                yield Operation.compareNumeric(sensorValue, baseValue, operation);
            }
        };

        log.info("Condition check: type={}, operation={}, baseValue={}, result={}",
                type, operation, baseValue, result);
        return result;
    }

    private DeviceActionRequest createActionRequest(Scenario scenario, Action action,
                                                    String sensorId, Instant timestamp) {
        log.info("Creating action request for scenario='{}', sensorId='{}', actionType='{}'",
                scenario.getName(), sensorId, action.getType());

        DeviceActionProto actionProto = DeviceActionProto.newBuilder()
                .setSensorId(sensorId)
                .setType(ActionTypeProto.valueOf(action.getType().name()))
                .setValue(action.getValue())
                .build();

        com.google.protobuf.Timestamp timestampProto = Timestamp.newBuilder()
                .setSeconds(timestamp.getEpochSecond())
                .setNanos(timestamp.getNano())
                .build();

        return DeviceActionRequest.newBuilder()
                .setHubId(scenario.getHubId())
                .setScenarioName(scenario.getName())
                .setAction(actionProto)
                .setTimestamp(timestampProto)
                .build();
    }

    private boolean extractBooleanValue(SensorStateAvro state, ConditionType type) {
        Object data = state.getData();
        return switch (type) {
            case SWITCH -> ((SwitchSensorAvro) data).getState();
            case MOTION -> ((MotionSensorAvro) data).getMotion();
            default -> throw new IllegalArgumentException("Unexpected condition type: " + type);
        };
    }

    private int extractNumericValue(SensorStateAvro state, ConditionType type) {
        Object data = state.getData();
        return switch (type) {
            case LUMINOSITY -> ((LightSensorAvro) data).getLuminosity();
            case HUMIDITY -> ((ClimateSensorAvro) data).getHumidity();
            case CO2LEVEL -> ((ClimateSensorAvro) data).getCo2Level();
            case TEMPERATURE -> {
                if (data instanceof ClimateSensorAvro) {
                    yield ((ClimateSensorAvro) data).getTemperatureC();
                } else if (data instanceof TemperatureSensorAvro) {
                    yield ((TemperatureSensorAvro) data).getTemperatureC();
                } else {
                    throw new IllegalArgumentException("Unexpected sensor data type: " + data.getClass());
                }
            }
            default -> throw new IllegalArgumentException("Unexpected condition type: " + type);
        };
    }
}