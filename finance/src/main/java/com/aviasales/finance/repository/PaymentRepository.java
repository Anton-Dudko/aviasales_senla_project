package com.aviasales.finance.repository;

import com.aviasales.finance.entity.Payment;
import com.aviasales.finance.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
    Optional<Payment> findByIdAndPaymentStatus(Long id, PaymentStatus paymentStatus);
}
