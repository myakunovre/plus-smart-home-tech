package ru.yandex.practicum.telemetry.collector.model.hub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAction {
    private String sensorId;
    private ActionType type;
    private Integer value;
}