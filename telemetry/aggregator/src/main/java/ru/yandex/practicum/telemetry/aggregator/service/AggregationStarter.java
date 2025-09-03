package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.config.KafkaConfig;

import java.time.Duration;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс AggregationStarter, ответственный за запуск агрегации данных.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    // ... объявление полей и конструктора ...
    private final SnapshotService snapshotService = new SnapshotServiceImpl();
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new ConcurrentHashMap<>();
    private final EnumMap<KafkaConfig.TopicType, String> topics = new EnumMap<>(KafkaConfig.TopicType.class);

    private final KafkaConsumer<String, SpecificRecordBase> consumer;
    private final KafkaProducer<String, SpecificRecordBase> producer;

    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);


    public AggregationStarter(KafkaConfig kafkaConfig) {
        this.consumer = new KafkaConsumer<>(kafkaConfig.getConsumer().getProperties());
        this.producer = new KafkaProducer<>(kafkaConfig.getProducer().getProperties());
        for (KafkaConfig.TopicType type : KafkaConfig.TopicType.values()) {
            topics.put(type, kafkaConfig.getTopic(type));
        }
    }

    /**
     * Метод для начала процесса агрегации данных.
     * Подписывается на топики для получения событий от датчиков,
     * формирует снимок их состояния и записывает в кафку.
     */
    public void start() {
        try {

            // ... подготовка к обработке данных ...
            // ... например, подписка на топик ...
            consumer.subscribe(List.of(topics.get(KafkaConfig.TopicType.SENSOR_EVENTS)));


            // Цикл обработки событий
            while (true) {
                // ... реализация цикла опроса ...
                // ... и обработка полученных данных ...
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    SensorEventAvro event = handleRecord(record);
                    Optional<SensorsSnapshotAvro> snapshot = snapshotService.updateState(event);
                    snapshot.ifPresent(this::sendSnapshot);

                    TopicPartition tp = new TopicPartition(record.topic(), record.partition());
                    currentOffsets.put(tp, new OffsetAndMetadata(record.offset() + 1));
                }
            }

        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {

            try {
                // Перед тем, как закрыть продюсер и консьюмер, нужно убедиться,
                // что все сообщения, лежащие в буффере, отправлены и
                // все оффсеты обработанных сообщений зафиксированы

                // здесь нужно вызвать метод продюсера для сброса данных в буффере
                // здесь нужно вызвать метод консьюмера для фиксиции смещений

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }

    private SensorEventAvro handleRecord(ConsumerRecord<String, SpecificRecordBase> record) {
        log.info("Received record: topic={}, partition={}, offset={}, value={}",
                record.topic(), record.partition(), record.offset(), record.value());
        if (!(record.value() instanceof SensorEventAvro)) {
            throw new IllegalArgumentException("Unexpected record type: " + record.value().getClass());
        }
        return (SensorEventAvro) record.value();
    }

    private void sendSnapshot(SensorsSnapshotAvro snapshot) {
        String topic = topics.get(KafkaConfig.TopicType.SNAPSHOT_EVENTS);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, snapshot);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Failed to send snapshot: {}", snapshot, exception);
            } else {
                log.info("Snapshot sent: topic={}, partition={}, offset={}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }
}
