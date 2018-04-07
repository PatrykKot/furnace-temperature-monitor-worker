package com.kotlarz.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @JoinColumn(name = "sensorid", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Sensor sensor;
}
