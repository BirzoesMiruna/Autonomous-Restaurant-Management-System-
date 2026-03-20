package com.example.restaurantscheduler.repository;

import com.example.restaurantscheduler.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    // Custom Query: Gaseste o masa dupa numar (pentru a verifica disponibilitatea)
    RestaurantTable findByNumber(Integer number);
}