package ru.yandex.practicum.telemetry.analyzer.dal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "conditions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "type")
    @Convert(converter = ConditionTypeConverter.class)
    private ConditionType type;

    @Column(name = "value")
    private int value;

    @Column(name = "operation")
    @Convert(converter = ConditionOperationConverter.class)
    private ConditionOperation operation;
}