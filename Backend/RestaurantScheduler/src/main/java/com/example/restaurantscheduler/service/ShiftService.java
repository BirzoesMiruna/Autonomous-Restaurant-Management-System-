package com.example.restaurantscheduler.service;

import com.example.restaurantscheduler.entity.Shift;
import com.example.restaurantscheduler.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDate;

@Service
//@RequiredArgsConstructor
public class ShiftService {

    private final ShiftRepository shiftRepository;
    // Pot injecta WaiterService daca am nevoie sa validez angajatii

    public ShiftService(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    // CRUD: CREATE/UPDATE
    public Shift saveShift(Shift shift) {
        // Poate fi apelat de AI sau de Manager
        return shiftRepository.save(shift);
    }

    // CUSTOM QUERY: Gaseste schimburile dupa data (util pentru manager/dashboard)
    public List<Shift> getShiftsByDate(LocalDate date) {
        return shiftRepository.findByScheduledDate(date);
    }

    // CRUD: DELETE
    public void deleteShift(Long id) {
        shiftRepository.deleteById(id);
    }

    //  Metoda pentru integrarea AI (Va fi implementata mai tarziu)
    public List<Shift> generateScheduleAI(LocalDate startDate, LocalDate endDate) {
        // AICI se face apelul HTTP catre modulul AI 
        System.out.println("DEBUG: Se face apelul catre modulul AI pentru a genera programul...");

        // Deocamdata, returnez o lista vida sau apelez o logica simpla de placeholder
        List<Shift> newShifts = List.of();

        // Dupa ce primesc datele de la AI, le salvez:
        // shiftRepository.saveAll(newShifts);
        return newShifts;
    }
}