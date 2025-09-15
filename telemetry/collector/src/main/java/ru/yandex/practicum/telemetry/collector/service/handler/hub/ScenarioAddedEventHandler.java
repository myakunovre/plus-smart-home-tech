package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.yandex.practicum.telemetry.collector.utils.ScenarioMapper;

import java.util.List;

@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {

    public ScenarioAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEventProto event) {
        ScenarioAddedEventProto _event = event.getScenarioAdded();

        List<ScenarioConditionAvro> avroConditions = ScenarioMapper.mapConditions(_event.getConditionsList());
        List<DeviceActionAvro> avroActions = ScenarioMapper.mapActions(_event.getActionsList());

        return ScenarioAddedEventAvro.newBuilder()
                .setName(_event.getName())
                .setConditions(avroConditions)
                .setActions(avroActions)
                .build();
    }
}
