package com.example.congestiontax.model.properties;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicHoliday {

    @CsvBindByName(column = "Name")
    private String name;

    @CsvBindByName(column = "LocalName")
    private String localName;

    @CsvBindByName(column = "Date")
    @CsvDate("yyyy-MM-dd")
    private LocalDate date;

}
