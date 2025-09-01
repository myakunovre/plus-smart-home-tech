package ru.yandex.practicum.telemetry.collector.service.handler;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventHandler {
    //    SensorEventType getMessageType();
    SensorEventProto.PayloadCase getMessageType();

    //    void handle(SensorEvent event);
    void handle(SensorEventProto event);
}
