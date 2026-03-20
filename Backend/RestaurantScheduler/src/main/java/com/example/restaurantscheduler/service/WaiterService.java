package com.example.restaurantscheduler.service;

import com.example.restaurantscheduler.entity.Waiter;
import com.example.restaurantscheduler.repository.WaiterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
//@RequiredArgsConstructor
public class WaiterService {

    // Injectarea Repository-ului
    private final WaiterRepository waiterRepository;

    public WaiterService(WaiterRepository waiterRepository) {
        this.waiterRepository = waiterRepository;
    }

    // CRUD: CREATE/UPDATE
    public Waiter saveWaiter(Waiter waiter) {
        // Aici pot adauga logica de validare inainte de salvare (ex: verificare email unic)
        return waiterRepository.save(waiter);
    }

    // CRUD: READ All
    public List<Waiter> getAllWaiters() {
        return waiterRepository.findAll();
    }

    // CRUD: READ by ID
    public Waiter getWaiterById(Long id) {
        return waiterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ospatarul nu a fost gasit!"));
    }

    // CRUD: DELETE
    public void deleteWaiter(Long id) {
        waiterRepository.deleteById(id);
    }

    // CUSTOM QUERY: Find by Email (exista in WaiterRepository)
    public Waiter getWaiterByEmail(String email) {
        return waiterRepository.findByEmail(email);
    }
}