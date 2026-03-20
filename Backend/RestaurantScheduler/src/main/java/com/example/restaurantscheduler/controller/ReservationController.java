package com.example.restaurantscheduler.controller;

import com.example.restaurantscheduler.entity.Reservation;
import com.example.restaurantscheduler.entity.RestaurantTable;
import com.example.restaurantscheduler.entity.Waiter;
import com.example.restaurantscheduler.repository.RestaurantTableRepository;
import com.example.restaurantscheduler.repository.WaiterRepository;
import com.example.restaurantscheduler.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final RestaurantTableRepository tableRepository;
    private final WaiterRepository waiterRepository;

    // --- 1. REZERVARI ---
    @PostMapping("/reservations")
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        if (reservation.getStatus() == null) {
            reservation.setStatus("PENDING");
        }
        return new ResponseEntity<>(reservationService.saveReservation(reservation), HttpStatus.CREATED);
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @PutMapping("/reservations/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation details) {
        Reservation existing = reservationService.getReservationById(id);
        if (existing == null) return ResponseEntity.notFound().build();

        existing.setCustomerName(details.getCustomerName());
        existing.setEmail(details.getEmail());
        existing.setReservationTime(details.getReservationTime());
        existing.setGuests(details.getGuests());
        existing.setStatus(details.getStatus());

        return ResponseEntity.ok(reservationService.saveReservation(existing));
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    // --- 2. MESE (TABLES) ---
    @GetMapping("/tables")
    public ResponseEntity<List<RestaurantTable>> getAllTables() {
        return ResponseEntity.ok(tableRepository.findAll());
    }

    @PostMapping("/tables")
    public ResponseEntity<RestaurantTable> createTable(@RequestBody RestaurantTable table) {
        return new ResponseEntity<>(tableRepository.save(table), HttpStatus.CREATED);
    }

    @DeleteMapping("/tables/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- 3. OSPATARI (WAITERS) ---
    @GetMapping("/waiters")
    public ResponseEntity<List<Waiter>> getAllWaiters() {
        return ResponseEntity.ok(waiterRepository.findAll());
    }

    @PostMapping("/waiters")
    public ResponseEntity<Waiter> createWaiter(@RequestBody Waiter waiter) {
        return new ResponseEntity<>(waiterRepository.save(waiter), HttpStatus.CREATED);
    }

    @DeleteMapping("/waiters/{id}")
    public ResponseEntity<Void> deleteWaiter(@PathVariable Long id) {
        waiterRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- 4. OPTIMIZARE AI  ---
    @PostMapping("/optimization/optimize")
    public ResponseEntity<String> optimize() {
        try {
            log.info("Inițiere proces optimizare prin Gemini AI...");
            reservationService.optimizeReservations();
            return ResponseEntity.ok("Optimizare AI finalizată cu succes.");
        } catch (Exception e) {
            log.error("Eroare la optimizarea AI: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Eroare la procesarea algoritmului AI: " + e.getMessage());
        }
    }
}