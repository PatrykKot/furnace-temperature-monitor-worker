package com.kotlarz.backend.persistance;

import com.kotlarz.backend.domain.TemperatureLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface TemperatureLogRepository extends JpaRepository<TemperatureLog, Long> {
    @Query("select u from TemperatureLog u where u.date > ?1")
    List<TemperatureLog> findLaterThan(Date date);
}
