package com.kotlarz.backend.persistance;

import com.kotlarz.backend.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
    Optional<Sensor> findByAddress(String address);
}
