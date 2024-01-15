package com.example.codebase.domain.location.repository;

import com.example.codebase.domain.location.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("SELECT l FROM Location l WHERE (l.latitude = :gpsX AND l.longitude = :gpsY) OR l.address = :address ")
    List<Location> findByGpsXAndGpsYOrAddress(String gpsX, String gpsY, String address);

    @Query(
            "SELECT l FROM Location l LEFT JOIN Member m ON l.member = m"
                    + " WHERE :keyword IS NULL OR l.address LIKE :keyword% " +
                    "OR :keyword IS NULL OR l.name LIKE :keyword% " +
                    "OR :keyword IS NULL OR m.username LIKE :keyword%")
    Page<Location> findByKeyword(String keyword, Pageable pageable);

}
