package ru.yandex.practicum.telemetry.collector.service.handler;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventHandler {
    //    HubEventType getMessageType();
    HubEventProto.PayloadCase getMessageType();

    //    void handle(HubEvent event);
    void handle(HubEventProto event);
}