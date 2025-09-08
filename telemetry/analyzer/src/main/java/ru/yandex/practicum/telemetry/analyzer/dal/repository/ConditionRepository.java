package ru.yandex.practicum.telemetry.analyzer.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemetry.analyzer.dal.entity.Condition;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
}