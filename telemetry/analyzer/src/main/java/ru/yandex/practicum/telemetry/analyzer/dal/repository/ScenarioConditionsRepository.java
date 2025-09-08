package ru.yandex.practicum.telemetry.analyzer.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.telemetry.analyzer.dal.entity.ScenarioCondition;
import ru.yandex.practicum.telemetry.analyzer.dal.entity.ScenarioConditionId;

public interface ScenarioConditionsRepository extends JpaRepository<ScenarioCondition, ScenarioConditionId> {
    @Modifying
    @Query("DELETE FROM ScenarioCondition sc WHERE sc.id.sensorId = :sensorId")
    void deleteBySensorId(@Param("sensorId") String sensorId);
}