package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.telemetry.collector.model.sensors.MotionSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensors.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensors.SensorEventType;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;

@Component
public class MotionSensorEventHandler  extends BaseSensorEventHandler<MotionSensorAvro> {

    public MotionSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }

    @Override
    protected MotionSensorAvro mapToAvro(SensorEvent event) {
        MotionSensorEvent _event = (MotionSensorEvent) event;
        return MotionSensorAvro.newBuilder()
                .setMotion(_event.isMotion())
                .setLinkQuality(_event.getLinkQuality())
                .setVoltage(_event.getVoltage())
                .build();
    }
}