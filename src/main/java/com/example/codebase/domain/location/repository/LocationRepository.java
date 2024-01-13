package com.example.codebase.domain.location.repository;

import com.example.codebase.domain.location.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query(
        "SELECT DISTINCT l FROM Location l"
            + " WHERE l.address LIKE :keyword% OR l.name LIKE :keyword%")
    Page<Location> findByKeyword(String keyword, Pageable pageable);

    @Query("SELECT l FROM Location l WHERE (l.latitude = :gpsX AND l.longitude = :gpsY) OR l.address = :address ")
    List<Location> findByGpsXAndGpsYOrAddress(String gpsX, String gpsY, String address);

    @Query("SELECT l FROM Location l WHERE l.address = :address")
    Optional<Location> findByName(String address);

    @Query("SELECT l FROM Location l LEFT JOIN l.member m WHERE "
            + "(:keyword IS NULL OR l.address LIKE :keyword% OR l.name LIKE :keyword%) "
            + "AND (:username IS NULL OR m.username = :username)")
    Page<Location> findByOptionalUserNameAndOptionalKeyword(String username, String keyword, Pageable pageable);


}
