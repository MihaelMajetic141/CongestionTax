package com.example.congestiontax.model.properties;

import com.example.congestiontax.model.enums.VehicleType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Component
@ConfigurationProperties(prefix = "congestion")
@Data
public class CongestionTaxProperties {
    private String city;
    private int year;
    private int maxDailyCharge;
    private Set<VehicleType> exemptVehicles;
    private List<TimeBand> timeBands = new ArrayList<>();
    private ExemptPeriods exemptPeriods = new ExemptPeriods();

    @Data
    public static class TimeBand {
        private LocalTime from;
        private LocalTime to;
        private int amount;
    }

    @Data
    public static class ExemptPeriods {
        private Period freeMonth;
        private boolean weekends;
        private String holidaysCsv;

        @Data
        public static class Period {
            private LocalDate start;
            private LocalDate end;
        }
    }
}
