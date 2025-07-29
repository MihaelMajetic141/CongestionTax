package com.example.congestiontax.service;

import com.example.congestiontax.model.properties.CongestionTaxProperties;
import com.example.congestiontax.model.Passage;
import com.example.congestiontax.model.properties.PublicHoliday;
import com.example.congestiontax.model.Vehicle;
import com.example.congestiontax.model.enums.VehicleType;
import com.example.congestiontax.repository.PassageRepository;
import com.example.congestiontax.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class CongestionTaxCalculatorService {

    private final VehicleRepository vehicleRepository;
    private final PassageRepository passageRepository;
    private final CongestionTaxProperties congestionTaxProperties;
    private final Set<VehicleType> exemptVehicles;
    private final int maxDailyCharge;
    private final Set<LocalDate> exemptDates = new HashSet<>();

    public CongestionTaxCalculatorService(
            CsvReaderService csvReaderService,
            CongestionTaxProperties congestionTaxProperties,
            VehicleRepository vehicleRepository,
            PassageRepository passageRepository
    ) {
        this.exemptVehicles = new HashSet<>(congestionTaxProperties.getExemptVehicles());
        this.maxDailyCharge = congestionTaxProperties.getMaxDailyCharge();
        this.vehicleRepository = vehicleRepository;
        this.passageRepository = passageRepository;

        // July
        exemptDates.addAll(
                congestionTaxProperties.getExemptPeriods().getFreeMonth().getStart()
                        .datesUntil(congestionTaxProperties.getExemptPeriods().getFreeMonth().getEnd().plusDays(1))
                        .collect(Collectors.toSet())
        );

        // Weekends
        if (congestionTaxProperties.getExemptPeriods().isWeekends()) {
            LocalDate start = LocalDate.of(congestionTaxProperties.getYear(), 1, 1);
            LocalDate end = LocalDate.of(congestionTaxProperties.getYear(), 12, 31);
            exemptDates.addAll(
                    start.datesUntil(end.plusDays(1))
                            .filter(localDate ->
                                    localDate.getDayOfWeek() == DayOfWeek.SATURDAY ||
                                    localDate.getDayOfWeek() == DayOfWeek.SUNDAY
                            ).collect(Collectors.toSet())
            );
        }
        // Holidays and days before
        List<PublicHoliday> publicHolidays = csvReaderService.readPublicHolidays(
                congestionTaxProperties.getExemptPeriods().getHolidaysCsv()
        );
        exemptDates.addAll(
                publicHolidays.stream()
                        .flatMap(holiday ->
                                Stream.of(holiday.getDate(), holiday.getDate().minusDays(1))
                        ).collect(Collectors.toSet())
        );
        this.congestionTaxProperties = congestionTaxProperties;
    }

    private int calculateTax(List<LocalDateTime> dailyPassages) {
        List<LocalDateTime> sortedPassages = dailyPassages.stream().sorted().toList();
        List<List<LocalDateTime>> groups = new ArrayList<>();
        List<LocalDateTime> currentGroup = new ArrayList<>();

        for (LocalDateTime passage : sortedPassages) {
            if (currentGroup.isEmpty()) currentGroup.add(passage);
            else {
                if (Duration.between(currentGroup.getFirst(), passage).toMinutes() <= 60) {
                    currentGroup.add(passage);
                } else {
                    groups.add(currentGroup);
                    currentGroup = new ArrayList<>(List.of(passage));
                }
            }
        }
        if (!currentGroup.isEmpty()) groups.add(currentGroup);

        int tax = groups.stream()
                .mapToInt(group ->
                        group.stream()
                                .mapToInt(p -> getCharge(p.toLocalTime()))
                                .max().orElse(0)
                ).sum();

        return Math.min(tax, maxDailyCharge);
    }

    private int getCharge(LocalTime time) {
        return congestionTaxProperties.getTimeBands().stream()
                .filter(timeBand -> !time.isBefore(timeBand.getFrom()) && !time.isAfter(timeBand.getTo()))
                .mapToInt(CongestionTaxProperties.TimeBand::getAmount)
                .findFirst().orElse(0);
    }

    public Integer calculateDailyTax(String registration, LocalDate date) throws Exception {
        if (date.getYear() != congestionTaxProperties.getYear())
            throw new Exception("Year does not match properties year");

        if (exemptDates.contains(date)) return 0;

        Optional<Vehicle> existingVehicleOptional = vehicleRepository.findByRegistration(registration);
        if (existingVehicleOptional.isEmpty())
            throw new Exception("Vehicle not found");

        Vehicle vehicle = existingVehicleOptional.get();
        if (exemptVehicles.contains(vehicle.getType()) || exemptDates.contains(date))
            return 0;

        List<Passage> passagesInDay = passageRepository.findPassagesByVehicleAndTimestampBetween(
                vehicle, date.atStartOfDay(), date.plusDays(1).atStartOfDay());
        if (passagesInDay.isEmpty()) return 0;

        List<LocalDateTime> passageTimestamps = passagesInDay.stream().map(Passage::getTimestamp).toList();
        return calculateTax(passageTimestamps);
    }

    public Integer calculateTaxBetweenDates(
            String registration,
            LocalDate startDate,
            LocalDate endDate
    ) throws Exception {

        if (!startDate.isBefore(endDate))
            throw new Exception("Invalid date range provided. Start date should be before end date!");

        Optional<Vehicle> vehicleOptional = vehicleRepository.findByRegistration(registration);
        if (vehicleOptional.isEmpty())
            throw new Exception("Vehicle not found!");

        Vehicle vehicle = vehicleOptional.get();
        if (exemptVehicles.contains(vehicle.getType())) return 0;

        List<Passage> passageList = passageRepository.findPassagesByVehicleAndTimestampBetween(
                vehicle, startDate.atStartOfDay(), endDate.atStartOfDay());
        if (passageList.isEmpty()) return 0;

        List<LocalDateTime> passageTimestamps = passageList.stream().map(Passage::getTimestamp).toList();
        Map<LocalDate, List<LocalDateTime>> passagesGroupedByDate = passageTimestamps.stream()
                .collect(Collectors.groupingBy(LocalDateTime::toLocalDate));

        AtomicInteger taxSum = new AtomicInteger();
        passagesGroupedByDate.forEach((date, passages) -> {
            if (!exemptDates.contains(passages.getFirst().toLocalDate())) {
                taxSum.addAndGet(calculateTax(passages));
            }
        });

        return taxSum.get();
    }

    public Vehicle saveNewVehicle(Vehicle vehicle) throws Exception {
        Optional<VehicleType> existingVehicleTypeOptional = Arrays.stream(VehicleType.values())
                .filter(vehicleType ->
                        vehicleType.toString().equals(vehicle.getType().toString().toUpperCase())
                ).findFirst();
        if (existingVehicleTypeOptional.isEmpty())
            throw new Exception("Vehicle type not found");

        return vehicleRepository.save(vehicle);
    }

    public Passage saveNewPassage(Passage passage) throws Exception {
        Optional<Passage> existingPassageOptional = passageRepository
                .findPassageByVehicleAndTimestamp(passage.getVehicle(), passage.getTimestamp());
        if (existingPassageOptional.isPresent())
            throw new Exception("Passage already exists");

        Optional<Vehicle> existingVehicleOptional = vehicleRepository
                .findByRegistration(passage.getVehicle().getRegistration());
        if (existingVehicleOptional.isEmpty())
            saveNewVehicle(passage.getVehicle());

        return passageRepository.save(passage);
    }

    public Map<String, AtomicInteger> calculateDailyTaxForAll(LocalDate date) {
        List<Passage> dailyPassages = passageRepository.findPassagesByTimestampBetween(
                date.atStartOfDay(), date.plusDays(1).atStartOfDay());
        if (dailyPassages.isEmpty()) return new HashMap<>();

        Map<Vehicle, List<Passage>> passagesGroupedByVehicle = dailyPassages.stream()
                .collect(Collectors.groupingBy(Passage::getVehicle));
        Map<String, AtomicInteger> registrationAndTaxMap = new HashMap<>();

        passagesGroupedByVehicle.forEach((vehicle, passages) -> {
            AtomicInteger taxSum = new AtomicInteger();
            if (exemptVehicles.contains(vehicle.getType()) || exemptDates.contains(date)) {
                registrationAndTaxMap.put(vehicle.getRegistration(), taxSum);
            } else {
                List<LocalDateTime> passageTimestamps = passages.stream().map(Passage::getTimestamp).toList();
                taxSum.addAndGet(calculateTax(passageTimestamps));
                registrationAndTaxMap.put(vehicle.getRegistration(), taxSum);
            }
        });

        return registrationAndTaxMap;
    }

    public Map<String, AtomicInteger> calculateTaxBetweenDatesForAll(LocalDate startDate, LocalDate endDate) {
        List<Passage> passageList = passageRepository.findPassagesByTimestampBetween(
                startDate.atStartOfDay(), endDate.atStartOfDay());
        if (passageList.isEmpty()) return new HashMap<>();

        Map<Vehicle, List<Passage>> passagesGroupedByVehicle = passageList.stream()
                .collect(Collectors.groupingBy(Passage::getVehicle));
        Map<String, AtomicInteger> registrationAndTaxMap = new HashMap<>();

        passagesGroupedByVehicle.forEach((vehicle, passages) -> {
            AtomicInteger taxSum = new AtomicInteger();
            if (exemptVehicles.contains(vehicle.getType())) {
                registrationAndTaxMap.put(vehicle.getRegistration(), taxSum);
            }
            else {
                List<LocalDateTime> passageTimestamps = passages.stream().map(Passage::getTimestamp).toList();
                Map<LocalDate, List<LocalDateTime>> passagesGroupedByDate = passageTimestamps.stream()
                        .collect(Collectors.groupingBy(LocalDateTime::toLocalDate));

                passagesGroupedByDate.forEach((date, timestamps) -> {
                    if (!(exemptDates.contains(timestamps.getFirst().toLocalDate()))) {
                        taxSum.addAndGet(calculateTax(timestamps));
                    }
                });
                registrationAndTaxMap.put(vehicle.getRegistration(), taxSum);
            }
        });

        return registrationAndTaxMap;
    }
}
