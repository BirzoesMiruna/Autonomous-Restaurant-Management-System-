package com.example.restaurantscheduler.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Data // Genereaza automat getters, setters, toString, etc.
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email")
    private String email;

    @Column(name = "reservation_time", nullable = false)
    private LocalDateTime reservationTime;

    @Column(name = "status")
    private String status; // Ex: "PENDING", "CONFIRMED"

    // --- CÂMPUL NOU ADĂUGAT ---
    @Column(name = "guests")
    private Integer guests; // Rezolva eroarea: Cannot resolve method 'getGuests'
    // --------------------------

    // Relatia cu Masa
    @ManyToOne
    @JoinColumn(name = "table_id")
    private RestaurantTable restaurantTable;

    // Relatia cu Ospatarul
    @ManyToOne
    @JoinColumn(name = "waiter_id")
    private Waiter waiter;
}