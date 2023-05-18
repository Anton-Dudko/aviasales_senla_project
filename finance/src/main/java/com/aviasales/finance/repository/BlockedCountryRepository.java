package com.aviasales.finance.repository;

import com.aviasales.finance.entity.BlockedCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockedCountryRepository extends JpaRepository<BlockedCountry, Long> {
    Optional<BlockedCountry> findByCountryCode(String countryCode);

}
