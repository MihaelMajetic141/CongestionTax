package com.example.congestiontax.model;

import com.example.congestiontax.model.enums.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @Column(length = 20)
    private String registration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

}