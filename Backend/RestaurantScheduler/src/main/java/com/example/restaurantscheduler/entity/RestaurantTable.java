package com.example.restaurantscheduler.entity;

import com.fasterxml.jackson.annotation.JsonIgnore; // <--- 1. IMPORT IMPORTANT
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "restaurant_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_number", unique = true, nullable = false)
    private Integer number;

    @Column(name = "seats", nullable = false)
    private Integer seats;

    // --- AICI OPRIM BUCLA INFINITA ---
    @OneToMany(mappedBy = "restaurantTable", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // <--- 2. ACEASTA ESTE LINIA MAGICĂ
    private List<Reservation> reservations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waiter_id")
    @JsonIgnore // <--- 3. RECOMANDAT:ca sa nu faca bucla si cu ospatarul
    private Waiter assignedWaiter;
}