package ru.yandex.practicum.telemetry.analyzer.dal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sensors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "hub_id")
    private String hubId;
}