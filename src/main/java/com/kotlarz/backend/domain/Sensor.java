package com.kotlarz.backend.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String address;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TemperatureLog> temperatureLogs;
}
