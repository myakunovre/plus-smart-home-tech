package ru.yandex.practicum.telemetry.collector.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaConfig;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
public class KafkaEventProducer implements AutoCloseable {

    protected final KafkaProducer<String, SpecificRecordBase> producer;

    public KafkaEventProducer(KafkaConfig kafkaConfig) {
        this.producer = new KafkaProducer<>(kafkaConfig.getProperties());
    }

    public void send(SpecificRecordBase event, String hubId, Instant timeStamp, String topic) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                null,
                timeStamp.toEpochMilli(),
                hubId,
                event
        );

        log.trace("Сохраняю событие {}, связанное с хабом {}, в топик {}",
                event.getClass().getSimpleName(), hubId, topic);

        log.info("<== Json: {}", event);
        producer.send(record);
    }

    public void close() {
        producer.flush();
        producer.close(Duration.ofSeconds(10));
    }
}
