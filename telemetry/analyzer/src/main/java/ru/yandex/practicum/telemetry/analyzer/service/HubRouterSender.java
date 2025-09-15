package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Service
@Slf4j
public class HubRouterSender {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public HubRouterSender(@GrpcClient("hub-router")
                           HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    public void sendAction(DeviceActionRequest action) {
        String hubId = action.getHubId();
        String sensorId = action.getAction().getSensorId();
        ActionTypeProto actionType = action.getAction().getType();

        try {
            hubRouterClient.handleDeviceAction(action);
            log.debug("Successfully sent action to hub '{}': sensor='{}', action='{}'",
                    hubId, sensorId, actionType);
        } catch (Exception e) {
            log.error("Failed to send action to hub '{}': {}", hubId, e.getMessage(), e);
        }
    }
}