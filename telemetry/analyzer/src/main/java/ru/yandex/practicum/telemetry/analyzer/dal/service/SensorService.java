package ru.yandex.practicum.telemetry.analyzer.dal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.telemetry.analyzer.dal.entity.Sensor;
import ru.yandex.practicum.telemetry.analyzer.dal.repository.ScenarioActionsRepository;
import ru.yandex.practicum.telemetry.analyzer.dal.repository.ScenarioConditionsRepository;
import ru.yandex.practicum.telemetry.analyzer.dal.repository.SensorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorService {
    private final SensorRepository sensorRepository;
    private final ScenarioActionsRepository scenarioActionsRepository;
    private final ScenarioConditionsRepository scenarioConditionsRepository;

    @Transactional
    public void addSensor(String sensorId, String hubId) {
        if (sensorRepository.existsByIdInAndHubId(List.of(sensorId), hubId)) {
            log.warn("Sensor with id {} already exist in hub {}", sensorId, hubId);
            return;
        }
        Sensor sensor = new Sensor(sensorId, hubId);
        sensorRepository.save(sensor);
    }

    @Transactional
    public void removeSensor(String sensorId) {
        Sensor sensor = sensorRepository.findById(sensorId).orElse(null);
        if (sensor == null) {
            log.warn("Sensor with id {} not found", sensorId);
            return;
        }

        scenarioConditionsRepository.deleteBySensorId(sensorId);
        scenarioActionsRepository.deleteBySensorId(sensorId);
        sensorRepository.deleteById(sensorId);
    }
}