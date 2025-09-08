package ru.yandex.practicum.telemetry.analyzer.dal.entity;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioConditionId implements Serializable {
    @Column(name = "scenario_id")
    private long scenarioId;

    @Column(name = "sensor_id")
    private String sensorId;

    @Column(name = "condition_id")
    private long conditionId;
}