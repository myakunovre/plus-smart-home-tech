package ru.yandex.practicum.telemetry.analyzer.dal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "actions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Convert(converter = ActionTypeConverter.class)
    @Column(name = "type")
    private ActionType type;

    @Column(name = "value")
    private int value;
}