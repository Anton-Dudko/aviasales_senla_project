package com.aviasales.finance.repository;

import com.aviasales.finance.entity.BlockedCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockedCardsRepository extends JpaRepository<BlockedCard, Long> {
    Optional<BlockedCard> findByCardNumber(String cardNumber);
}
