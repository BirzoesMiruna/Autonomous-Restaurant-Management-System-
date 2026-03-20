package com.example.restaurantscheduler.service;

import com.example.restaurantscheduler.entity.Reservation;
import com.example.restaurantscheduler.entity.RestaurantTable;
import com.example.restaurantscheduler.entity.Waiter;
import com.example.restaurantscheduler.repository.ReservationRepository;
import com.example.restaurantscheduler.repository.RestaurantTableRepository;
import com.example.restaurantscheduler.repository.WaiterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OptimizationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository tableRepository;
    private final WaiterRepository waiterRepository;

    private static final int RESERVATION_DURATION_HOURS = 2;

    public void optimizeSchedule() {
        // Iau doar rezervarile care asteapta(PENDING)
        List<Reservation> pendingReservations = reservationRepository.findByStatus("PENDING");
        List<RestaurantTable> tables = tableRepository.findAll();
        List<Waiter> waiters = waiterRepository.findAll();

        if (tables.isEmpty() || waiters.isEmpty()) return;

        // Sortez mesele dupa capacitate (Best Fit)
        tables.sort((t1, t2) -> t1.getSeats().compareTo(t2.getSeats()));

        int waiterIndex = 0;

        for (Reservation res : pendingReservations) {
            if (res.getRestaurantTable() == null) {
                int guests = res.getGuests() != null ? res.getGuests() : 2;
                LocalDateTime resTime = res.getReservationTime();
                RestaurantTable bestTable = null;

                for (RestaurantTable table : tables) {
                    // 1. Verifica dacă încap oamenii la masa
                    if (table.getSeats() >= guests) {
                        // 2. Verifica daca masa e libera
                        if (!checkIfTableIsOccupied(table.getId(), resTime)) {
                            bestTable = table;
                            break;
                        }
                    }
                }

                if (bestTable != null) {
                    res.setRestaurantTable(bestTable);
                    res.setWaiter(waiters.get(waiterIndex % waiters.size()));
                    waiterIndex++;
                    res.setStatus("CONFIRMED");
                    reservationRepository.save(res);
                }
            }
        }
    }

    private boolean checkIfTableIsOccupied(Long tableId, LocalDateTime targetTime) {
        // Definim fereastra de timp de 2 ore
        LocalDateTime startCheck = targetTime.minusHours(RESERVATION_DURATION_HOURS).plusMinutes(1);
        LocalDateTime endCheck = targetTime.plusHours(RESERVATION_DURATION_HOURS).minusMinutes(1);

        // ATENȚIE: Aici am pus numele EXACT ca în Repository-ul de mai sus
        List<Reservation> conflicts = reservationRepository.findByRestaurantTableIdAndReservationTimeBetween(
                tableId,
                startCheck,
                endCheck
        );

        return !conflicts.isEmpty();
    }
}