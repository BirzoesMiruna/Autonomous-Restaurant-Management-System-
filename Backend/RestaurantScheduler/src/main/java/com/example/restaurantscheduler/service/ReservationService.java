package com.example.restaurantscheduler.service;

import com.example.restaurantscheduler.entity.Reservation;
import com.example.restaurantscheduler.entity.RestaurantTable;
import com.example.restaurantscheduler.entity.Waiter;
import com.example.restaurantscheduler.repository.ReservationRepository;
import com.example.restaurantscheduler.repository.RestaurantTableRepository;
import com.example.restaurantscheduler.repository.WaiterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository tableRepository;
    private final WaiterRepository waiterRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              RestaurantTableRepository tableRepository,
                              WaiterRepository waiterRepository) {
        this.reservationRepository = reservationRepository;
        this.tableRepository = tableRepository;
        this.waiterRepository = waiterRepository;
    }

    @Transactional
    public void optimizeReservations() {
        System.out.println("!!! METODA OPTIMIZE A FOST APELATA !!!");

        List<Reservation> allReservations = reservationRepository.findAll();
        System.out.println("Total rezervari in DB: " + allReservations.size());

        // Select doar cele care nu au masa alocată
        List<Reservation> toProcess = allReservations.stream()
                .filter(r -> r.getRestaurantTable() == null)
                .collect(Collectors.toList());

        System.out.println("Rezervari fara masa gasite: " + toProcess.size());

        if (toProcess.isEmpty()) return;

        List<RestaurantTable> tables = tableRepository.findAll();
        List<Waiter> waiters = waiterRepository.findAll();

        for (Reservation res : toProcess) {
            // Algoritmul Best-Fit (cea mai mica masa care încape grupul)
            Optional<RestaurantTable> bestTable = tables.stream()
                    .filter(t -> t.getSeats() >= res.getGuests())
                    .min(Comparator.comparingInt(RestaurantTable::getSeats));

            if (bestTable.isPresent()) {
                res.setRestaurantTable(bestTable.get());
                res.setStatus("CONFIRMED");
                if (!waiters.isEmpty()) {
                    res.setWaiter(waiters.get(0));
                }
                reservationRepository.saveAndFlush(res);
                System.out.println("SUCCES: Alocat Masa " + bestTable.get().getId() + " pentru RezID " + res.getId());
            }
        }
    }

    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }
}