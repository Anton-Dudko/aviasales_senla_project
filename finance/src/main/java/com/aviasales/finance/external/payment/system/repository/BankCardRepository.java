package com.aviasales.finance.external.payment.system.repository;

import com.aviasales.finance.external.payment.system.enity.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankCardRepository extends JpaRepository<BankCard, Long> {
    Optional<BankCard> findBankCardByCardNumber(String cardNumber);
}
