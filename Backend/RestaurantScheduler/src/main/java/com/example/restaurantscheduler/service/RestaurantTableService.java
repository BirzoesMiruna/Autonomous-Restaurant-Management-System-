package com.example.restaurantscheduler.service;

import com.example.restaurantscheduler.entity.RestaurantTable;
import com.example.restaurantscheduler.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
//@RequiredArgsConstructor
public class RestaurantTableService {

    private final RestaurantTableRepository tableRepository;

    public RestaurantTableService(RestaurantTableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    // CRUD: CREATE/UPDATE
    public RestaurantTable saveTable(RestaurantTable table) {
        // Logica de business: verifica daca numarul mesei este deja folosit
        if (tableRepository.findByNumber(table.getNumber()) != null && table.getId() == null) {
            throw new RuntimeException("Masa cu numarul " + table.getNumber() + " exista deja.");
        }
        return tableRepository.save(table);
    }

    // CRUD: READ All
    public List<RestaurantTable> getAllTables() {
        return tableRepository.findAll();
    }

    // CRUD: READ by ID
    public RestaurantTable getTableById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Masa nu a fost gasita!"));
    }

    // CRUD: DELETE
    public void deleteTable(Long id) {
        tableRepository.deleteById(id);
    }

}