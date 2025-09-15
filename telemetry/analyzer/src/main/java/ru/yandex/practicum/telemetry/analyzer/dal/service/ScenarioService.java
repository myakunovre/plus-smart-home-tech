package ru.yandex.practicum.telemetry.analyzer.dal.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.analyzer.dal.entity.*;
import ru.yandex.practicum.telemetry.analyzer.dal.repository.ActionRepository;
import ru.yandex.practicum.telemetry.analyzer.dal.repository.ConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.dal.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.dal.repository.SensorRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioService {
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final SensorRepository sensorRepository;

    @Transactional
    public void addScenario(ScenarioAddedEventAvro scenario, String hubId) {
        if (scenarioRepository.findByHubIdAndName(hubId, scenario.getName()).isPresent()) {
            log.warn("Scenario with name '{}' already exist in hub '{}'", scenario.getName(), hubId);
            return;
        }

        // Сбор всех sensorId для пакетной проверки
        Set<String> allSensorIds = scenario.getConditions().stream()
                .map(ScenarioConditionAvro::getSensorId)
                .collect(Collectors.toSet());
        allSensorIds.addAll(scenario.getActions().stream()
                .map(DeviceActionAvro::getSensorId)
                .collect(Collectors.toSet()));

        // Пакетная проверка существования всех сенсоров
        List<Sensor> existingSensors = sensorRepository.findAllById(allSensorIds);
        if (existingSensors.size() != allSensorIds.size()) {
            Set<String> foundSensorIds = existingSensors.stream()
                    .map(Sensor::getId)
                    .collect(Collectors.toSet());
            allSensorIds.forEach(sensorId -> {
                if (!foundSensorIds.contains(sensorId)) {
                    log.warn("Sensor with id '{}' not found in hub '{}'", sensorId, hubId);
                }
            });
            throw new IllegalArgumentException("One or more sensors not found.");
        }

        Scenario scenarioEntity = new Scenario();
        scenarioEntity.setName(scenario.getName());
        scenarioEntity.setHubId(hubId);

        Map<String, Condition> conditionMap = new HashMap<>();
        List<Condition> conditionsToSave = new ArrayList<>();
        List<ScenarioConditionAvro> conditionsAvro = scenario.getConditions();
        for (ScenarioConditionAvro conditionAvro : conditionsAvro) {
            String sensorId = conditionAvro.getSensorId();
            Condition condition = new Condition();
            condition.setType(ConditionType.valueOf(conditionAvro.getType().name()));
            condition.setOperation(ConditionOperation.valueOf(conditionAvro.getOperation().name()));
            if (conditionAvro.getValue() != null) {
                Object rawValue = conditionAvro.getValue();
                if (rawValue instanceof Boolean) {
                    condition.setValue((Boolean) rawValue ? 1 : 0);
                } else if (rawValue instanceof Integer) {
                    condition.setValue((Integer) rawValue);
                } else {
                    throw new IllegalArgumentException("Unexpected condition value type: " + rawValue.getClass());
                }
            }
            conditionsToSave.add(condition);
            conditionMap.put(sensorId, condition);
        }
        conditionRepository.saveAll(conditionsToSave);
        scenarioEntity.setConditions(conditionMap);

        Map<String, Action> actionsMap = new HashMap<>();
        List<Action> actionsToSave = new ArrayList<>();
        List<DeviceActionAvro> actionsAvro = scenario.getActions();
        for (DeviceActionAvro actionAvro : actionsAvro) {
            String sensorId = actionAvro.getSensorId();
            Action action = new Action();
            action.setType(ActionType.valueOf(actionAvro.getType().name()));
            if (actionAvro.getValue() != null) {
                action.setValue(actionAvro.getValue());
            }
            actionsToSave.add(action);
            actionsMap.put(sensorId, action);
        }
        actionRepository.saveAll(actionsToSave);
        scenarioEntity.setActions(actionsMap);

        scenarioRepository.save(scenarioEntity);
    }

    @Transactional
    public void removeScenario(String scenarioName, String hubId) {
        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, scenarioName).orElse(null);
        if (scenario == null) {
            log.warn("Scenario with name '{}' not found in hub '{}'", scenarioName, hubId);
            return;
        }

        List<Long> conditionIds = scenario.getConditions().values().stream()
                .map(Condition::getId)
                .toList();

        List<Long> actionIds = scenario.getActions().values().stream()
                .map(Action::getId)
                .toList();

        scenario.getConditions().clear();
        scenario.getActions().clear();
        scenarioRepository.save(scenario);
        scenarioRepository.deleteById(scenario.getId());

        if (!conditionIds.isEmpty()) {
            conditionRepository.deleteAllById(conditionIds);
        }

        if (!actionIds.isEmpty()) {
            actionRepository.deleteAllById(actionIds);
        }
    }
}