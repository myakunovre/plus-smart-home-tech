package ru.yandex.practicum.telemetry.analyzer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.dal.service.ScenarioService;
import ru.yandex.practicum.telemetry.analyzer.dal.service.SensorService;

@Service
@RequiredArgsConstructor
public class HubEventService {
    private final SensorService sensorService;
    private final ScenarioService scenarioService;

    @Transactional
    public void handleEvent(HubEventAvro hubEvent) {
        String hubId = hubEvent.getHubId();
        Object payload = hubEvent.getPayload();

        switch (payload) {
            case DeviceAddedEventAvro event -> sensorService.addSensor(event.getId(), hubId);
            case DeviceRemovedEventAvro event -> sensorService.removeSensor(event.getId());
            case ScenarioAddedEventAvro event -> scenarioService.addScenario(event, hubId);
            case ScenarioRemovedEventAvro event -> scenarioService.removeScenario(event.getName(), hubId);
            default -> throw new IllegalArgumentException("Unexpected event type: " + payload.getClass());
        }
    }
}