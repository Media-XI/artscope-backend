package com.example.codebase.domain.location.repository;

import com.example.codebase.domain.location.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query(
        "SELECT DISTINCT l FROM Location l"
            + " WHERE l.address LIKE :keyword% OR l.name LIKE :keyword%")
    Page<Location> findByKeyword(String keyword, Pageable pageable);

    @Query("SELECT l FROM Location l WHERE l.latitude = :gpsX AND l.longitude = :gpsY ")
    Optional<Location> findByGpsXAndGpsY(String gpsX, String gpsY);

    @Query("SELECT l FROM Location l WHERE l.name = :name")
    Optional<Location> findByName(String name);

}
