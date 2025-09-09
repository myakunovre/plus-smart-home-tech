package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.config.KafkaConfig;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class SnapshotProcessor implements Runnable, DisposableBean {
    private final KafkaConsumer<String, SpecificRecordBase> consumer;
    private final SnapshotService snapshotService;
    private final String topic;
    private final AtomicBoolean running = new AtomicBoolean(true); // Флаг для контроля цикла

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new ConcurrentHashMap<>();
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    public SnapshotProcessor(KafkaConfig config, SnapshotService snapshotService) {
        this.consumer = new KafkaConsumer<>(config.getSnapshotConsumer().getProperties());
        this.snapshotService = snapshotService;
        this.topic = config.getTopic(KafkaConfig.TopicType.SNAPSHOT_EVENTS);
    }

    public void run() {
        log.info("SnapshotProcessor started");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown hook triggered. Waking up snapshotConsumer...");
            consumer.wakeup();
        }));
        try {
            consumer.subscribe(List.of(topic));
            while (running.get()) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    SensorsSnapshotAvro snapshot = handleRecord(record);
                    snapshotService.handleSnapshot(snapshot);
                    TopicPartition tp = new TopicPartition(record.topic(), record.partition());
                    currentOffsets.put(tp, new OffsetAndMetadata(record.offset() + 1));
                }

                if (!currentOffsets.isEmpty()) {
                    consumer.commitAsync(new HashMap<>(currentOffsets), (offsets, exception) -> {
                        if (exception != null) {
                            log.warn("Failed to commit offsets in snapshotConsumer: {}", offsets, exception);
                        }
                    });
                }
            }
        } catch (WakeupException e) {
            log.info("snapshotConsumer shutdown detected.");
        } catch (Exception e) {
            log.error("Error in SnapshotProcessor", e);
        } finally {
            try {
                if (!currentOffsets.isEmpty()) {
                    consumer.commitSync(currentOffsets);
                }
            } finally {
                log.info("Closing snapshotConsumer");
                consumer.close();
            }
        }
    }

    private SensorsSnapshotAvro handleRecord(ConsumerRecord<String, SpecificRecordBase> record) {
        log.info("Received snapshot record: topic={}, partition={}, offset={}, value={}",
                record.topic(), record.partition(), record.offset(), record.value());
        if (!(record.value() instanceof SensorsSnapshotAvro)) {
            throw new IllegalArgumentException("Unexpected record type: " + record.value().getClass());
        }
        return (SensorsSnapshotAvro) record.value();
    }

    @Override
    public void destroy() {
        log.info("SnapshotProcessor: Destroy method called. Attempting to stop consumer.");
        running.set(false);
        consumer.wakeup();
    }
}