package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotServiceImpl implements SnapshotService {

    private final Map<String, SensorsSnapshotAvro> sensors = new HashMap<>();

    @Override
    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        log.info("update state starting");
        final SensorsSnapshotAvro snapshot = sensors.computeIfAbsent(
                event.getHubId(),
                hubId ->
                        SensorsSnapshotAvro.newBuilder()
                                .setHubId(hubId)
                                .setTimestamp(event.getTimestamp())
                                .setSensorsState(new HashMap<>())
                                .build()
        );

        Map<String, SensorStateAvro> sensorState = snapshot.getSensorsState();

        if (sensorState.containsKey(event.getId())) {
            log.info("Данные сенсоров ранее уже были, сравниваем новое событие с текущим состоянием");
            SensorStateAvro oldState = sensorState.get(event.getId());
            if (oldState.getTimestamp().isAfter(event.getTimestamp()) ||
                    oldState.getData().equals(event.getPayload())) {
                log.info("Обновление текущего состояния не требуется");
                return Optional.empty();
            }
        }

        log.info("Обновление текущего состояния требуется, сейчас буду обновлять");
        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
        sensorState.put(event.getId(), newState);

        snapshot.setTimestamp(newState.getTimestamp());
        log.info("Обновление текущего состояния выполнено");
        return Optional.of(snapshot);
    }
}
