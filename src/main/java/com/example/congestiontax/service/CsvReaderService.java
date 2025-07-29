package com.example.congestiontax.service;

import com.example.congestiontax.model.properties.PublicHoliday;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Objects;


@Service
public class CsvReaderService {

    public List<PublicHoliday> readPublicHolidays(String filePath) {

        try (Reader reader = new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(filePath)))
        ) {

            HeaderColumnNameMappingStrategy<PublicHoliday> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(PublicHoliday.class);

            return new CsvToBeanBuilder<PublicHoliday>(reader)
                    .withMappingStrategy(strategy)
                    .withType(PublicHoliday.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
