package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.yandex.practicum.telemetry.collector.service.handler.HubEventHandler;

import java.time.Instant;

import static ru.yandex.practicum.telemetry.collector.config.KafkaConfig.TopicType.HUBS_EVENTS;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    protected final KafkaEventProducer producer;

    protected abstract T mapToAvro(HubEventProto event);

    @Override
    public void handle(HubEventProto event) {
        if (!event.getPayloadCase().equals(getMessageType())) {
            throw new IllegalArgumentException("Неизвестный тип события: " + event.getPayloadCase());
        }

        T payload = mapToAvro(event);

        com.google.protobuf.Timestamp protoTimestamp = event.getTimestamp();
        Instant instant = Instant.ofEpochSecond(
                protoTimestamp.getSeconds(),
                protoTimestamp.getNanos()
        );

        HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(instant)
                .setPayload(payload)
                .build();

        producer.send(eventAvro, event.getHubId(), instant, HUBS_EVENTS);
    }
}
