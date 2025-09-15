package ru.yandex.practicum.telemetry.analyzer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.dal.entity.Scenario;
import ru.yandex.practicum.telemetry.analyzer.dal.repository.ScenarioRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class SnapshotService {
    private final ScenarioRepository scenarioRepository;
    private final SnapshotAnalyzer snapshotAnalyzer;
    private final HubRouterSender hubRouterSender;

    @Transactional
    public void handleSnapshot(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);
        if (scenarios == null || scenarios.isEmpty()) {
            log.info("No scenarios found for hub '{}'", hubId);
            return;
        }

        List<DeviceActionRequest> actionsRequest = snapshotAnalyzer.analyze(snapshot, scenarios);
        for (DeviceActionRequest actionRequest : actionsRequest) {
            hubRouterSender.sendAction(actionRequest);
        }
    }

}