package com.example.restaurantscheduler.repository;

import com.example.restaurantscheduler.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByStatus(String status);

    // Varianta cu 4 parametri (folosita in AI)
    List<Reservation> findByRestaurantTableIdAndStatusAndReservationTimeBetween(
            Long restaurantTableId, String status, LocalDateTime start, LocalDateTime end);

    // REZOLVARE EROAREA : Varianta cu 3 parametri (pe care o cauta eroarea "cannot find symbol")
    List<Reservation> findByRestaurantTableIdAndReservationTimeBetween(
            Long restaurantTableId, LocalDateTime start, LocalDateTime end);

    List<Reservation> findByWaiterIdAndReservationTimeBetween(Long waiterId, LocalDateTime start, LocalDateTime end);

    List<Reservation> findByRestaurantTableId(Long restaurantTableId);
}