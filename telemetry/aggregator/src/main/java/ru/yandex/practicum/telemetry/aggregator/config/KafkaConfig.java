package ru.yandex.practicum.telemetry.aggregator.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Properties;

@Getter
@Setter
@ToString
//@Configuration
@ConfigurationProperties("aggregator.kafka")
public class KafkaConfig {
    private Producer producer;
    private Consumer consumer;
    private Map<String, String> topics;

    @Getter
    @Setter
    public static class Consumer {
        private Properties properties;
    }

    @Getter
    @Setter
    public static class Producer {
        private Properties properties;
    }

//    @Getter
//    public static class ProducerConfig {
//        private final Properties properties;
//        private final EnumMap<TopicType, String> topics = new EnumMap<>(TopicType.class);
//
//        public ProducerConfig(Properties properties, Map<String, String> topics) {
//            this.properties = properties;
//            for (Map.Entry<String, String> entry : topics.entrySet()) {
//                this.topics.put(TopicType.from(entry.getKey()), entry.getValue());
//            }
//        }
//    }

//    @Getter
//    public static class ConsumerConfig {
//        private final Properties properties;
//        private final EnumMap<TopicType, String> topics = new EnumMap<>(TopicType.class);
//
//        public ConsumerConfig(Properties properties, Map<String, String> topics) {
//            this.properties = properties;
//            for (Map.Entry<String, String> entry : topics.entrySet()) {
//                this.topics.put(TopicType.from(entry.getKey()), entry.getValue());
//            }
//        }
//    }

    public enum TopicType {
        SENSOR_EVENTS("sensors-events"),
        SNAPSHOT_EVENTS("snapshots-events");

        private final String topicName;

        TopicType(String topicName) {
            this.topicName = topicName;
        }

        public String getTopicName() {
            return topicName;
        }
    }

    public String getTopic(TopicType type) {
        String topic = topics.get(type.getTopicName());
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("Неизвестный тип топика: " + type);
        }
        return topic;
    }
}