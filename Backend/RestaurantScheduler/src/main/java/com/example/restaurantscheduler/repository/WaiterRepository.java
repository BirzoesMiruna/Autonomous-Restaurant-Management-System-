package com.example.restaurantscheduler.repository;

import com.example.restaurantscheduler.entity.Waiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WaiterRepository extends JpaRepository<Waiter, Long> {

    // Aici pot adauga o metoda Custom Query (exemplu: findByEmail)
    Waiter findByEmail(String email);
}