package com.migros.couriertracking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "couriers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Courier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "courier_id", unique = true, nullable = false)
    private String courierId;

    @Column(name = "total_distance", nullable = false)
    @Builder.Default
    private Double totalDistance = 0.0;

    @Column(name = "last_latitude")
    private Double lastLatitude;

    @Column(name = "last_longitude")
    private Double lastLongitude;
}
