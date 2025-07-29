package com.example.congestiontax.repository;

import com.example.congestiontax.model.Passage;
import com.example.congestiontax.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PassageRepository extends JpaRepository<Passage, Long> {

    List<Passage> findPassagesByVehicleAndTimestampBetween(Vehicle vehicle, LocalDateTime date1, LocalDateTime date2);
    List<Passage> findPassagesByTimestampBetween(LocalDateTime date1, LocalDateTime date2);
    Optional<Passage> findPassageByVehicleAndTimestamp(Vehicle vehicle, LocalDateTime date);

}
