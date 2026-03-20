package com.example.restaurantscheduler.entity;

import com.fasterxml.jackson.annotation.JsonIgnore; // <--- IMPORT OBLIGATORIU
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "waiter")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Waiter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "hourly_rate")
    private Double hourlyRate;

    // Relatia One-to-Many: Un ospatar poate avea multe schimburi
    @OneToMany(mappedBy = "waiter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // <--- ADAUGAT (Ca sa nu crape cand cer lista de ospatari)
    private List<Shift> shifts;

    // Relatia One-to-Many: Un ospatar gestioneaza multe rezervari
    @OneToMany(mappedBy = "waiter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // <--- ADAUGAT (CRITIC: Opreste bucla infinita cu Rezervarile)
    private List<Reservation> reservations;
}