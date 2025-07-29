package com.example.congestiontax.service;

import com.example.congestiontax.model.properties.PublicHoliday;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class CsvReaderServiceTest {

    private final CsvReaderService csvReaderService = new CsvReaderService();

    @Test
    void testReadPublicHolidays() {
        List<PublicHoliday> holidays = csvReaderService.readPublicHolidays("publicholiday.SE.2013.csv");

        holidays.forEach(System.out::println);

        assertEquals(16, holidays.size());
        assertEquals("New Year's Day", holidays.getFirst().getName());
        assertEquals("Nyårsdagen", holidays.getFirst().getLocalName());
        assertEquals(LocalDate.of(2013, 1, 1), holidays.getFirst().getDate());
    }
}
