package com.example.congestiontax.controller;


import com.example.congestiontax.model.Passage;
import com.example.congestiontax.model.Vehicle;
import com.example.congestiontax.service.CongestionTaxCalculatorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;


@RestController
@RequestMapping("/api/congestion")
@AllArgsConstructor
public class CongestionController {

    private final CongestionTaxCalculatorService congestionTaxCalculatorService;
    private static final Logger logger = LoggerFactory.getLogger(CongestionController.class);

    @PostMapping("/addPassage")
    public ResponseEntity<?> addPassage(@RequestBody Passage passage) {
        try {
            return ResponseEntity.ok(congestionTaxCalculatorService.saveNewPassage(passage));
        } catch (Exception e) {
            logger.error("Error adding passage: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/addVehicle")
    public ResponseEntity<?> addVehicle(@RequestBody Vehicle vehicle) {
        try {
            return ResponseEntity.ok(
                    congestionTaxCalculatorService.saveNewVehicle(vehicle));
        } catch (Exception e) {
            logger.error("Error adding vehicle: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/calculateDailyTax")
    public ResponseEntity<?> calculateDailyTax(
            @RequestParam String registration,
            @RequestParam LocalDate date
    ) {
        try {
            return ResponseEntity.ok(
                    congestionTaxCalculatorService.calculateDailyTax(registration, date)
            );
        } catch (Exception e) {
            logger.error("Calculate daily tax error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/calculateMonthlyTax")
    public ResponseEntity<?> calculateMonthlyTax(
            @RequestParam String registration,
            @RequestParam int month,
            @RequestParam int year
    ) {
        try {
            return ResponseEntity.ok(
                    congestionTaxCalculatorService.calculateTaxBetweenDates(
                            registration,
                            LocalDate.of(year, month, 1),
                            LocalDate.of(year, month + 1, 1))
            );
        } catch (Exception e) {
            logger.error("Calculate monthly tax error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/calculateYearlyTax")
    public ResponseEntity<?> calculateYearlyTax(
            @RequestParam String registration,
            @RequestParam int year
    ) {
        try {
            return ResponseEntity.ok(
                    congestionTaxCalculatorService.calculateTaxBetweenDates(
                            registration,
                            LocalDate.of(year, 1, 1),
                            LocalDate.of(year + 1, 1, 1))
            );
        } catch (Exception e) {
            logger.error("Calculate yearly tax error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/calculateDailyTaxForAll")
    public ResponseEntity<?> calculateDailyTaxForAll(
            @RequestParam LocalDate date
    ) {
        try {
            return ResponseEntity.ok(congestionTaxCalculatorService.calculateDailyTaxForAll(date));
        } catch (Exception e) {
            logger.error("Calculate daily tax for all error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/calculateMonthlyTaxForAll")
    public ResponseEntity<?> calculateMonthlyTaxForAll(
            @RequestParam int month,
            @RequestParam int year
    ) {
        try {
            return ResponseEntity.ok(
                    congestionTaxCalculatorService.calculateTaxBetweenDatesForAll(
                            LocalDate.of(year, month, 1),
                            LocalDate.of(year, month + 1, 1)
                    )
            );
        } catch (Exception e) {
            logger.error("Calculate monthly tax for all error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/calculateYearlyTaxForAll")
    public ResponseEntity<?> calculateYearlyTaxForAll(
            @RequestParam int year
    ) {
        try {
            return ResponseEntity.ok(
                    congestionTaxCalculatorService.calculateTaxBetweenDatesForAll(
                            LocalDate.of(year, 1, 1),
                            LocalDate.of(year + 1, 1, 1)
                    )
            );
        } catch (Exception e) {
            logger.error("Calculate yearly tax for all error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
