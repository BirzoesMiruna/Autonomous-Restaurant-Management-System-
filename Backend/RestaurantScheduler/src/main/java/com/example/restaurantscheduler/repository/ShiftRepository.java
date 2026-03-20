package com.example.restaurantscheduler.repository;

import com.example.restaurantscheduler.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDate;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    // Custom Query: Gaseste toate schimburile pentru o anumita zi (util pentru manager)
    List<Shift> findByScheduledDate(LocalDate date);
}