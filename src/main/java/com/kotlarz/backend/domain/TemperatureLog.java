package com.kotlarz.backend.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemperatureLog {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private Double value;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sensorid", nullable = false)
    private Sensor sensor;
}
