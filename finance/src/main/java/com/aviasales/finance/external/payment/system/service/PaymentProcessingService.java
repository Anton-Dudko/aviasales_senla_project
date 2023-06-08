package com.aviasales.finance.external.payment.system.service;

import com.aviasales.finance.dto.payment.RefundExternalDto;
import com.aviasales.finance.external.payment.system.converter.BankCardMapper;
import com.aviasales.finance.external.payment.system.dto.BankCardDto;
import com.aviasales.finance.external.payment.system.dto.PaymentForProcessingDto;
import com.aviasales.finance.external.payment.system.enity.BankCard;
import com.aviasales.finance.external.payment.system.exceptions.BankCardNotFoundException;
import com.aviasales.finance.external.payment.system.exceptions.CardDetailsMismatchException;
import com.aviasales.finance.external.payment.system.exceptions.ExternalAccountException;
import com.aviasales.finance.external.payment.system.exceptions.InsufficientBalanceException;
import com.aviasales.finance.external.payment.system.repository.BankCardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class PaymentProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentProcessingService.class);
    private final BankCardRepository bankCardRepository;

    @Autowired
    public PaymentProcessingService(BankCardRepository bankCardRepository) {
        this.bankCardRepository = bankCardRepository;
    }

    public void processPayment(PaymentForProcessingDto paymentForProcessingDto) {
        logger.info("checking card is exists");
        BankCard bankCard = bankCardRepository.findBankCardByCardNumber(paymentForProcessingDto
                .getCardNumber()).orElseThrow(() -> new BankCardNotFoundException("Such Bank card number not found - "
                + paymentForProcessingDto.getCardNumber()));

        logger.info("checking provided card details match");
        if (!checkCardDetailsAreMatch(paymentForProcessingDto, bankCard)) {
            throw new CardDetailsMismatchException("Card details do not match for card number "
                    + paymentForProcessingDto.getCardNumber());
        }

        logger.info("checking card balance enough to pay");
        if (!checkSumCanBePaid(bankCard, paymentForProcessingDto.getSum())) {
            throw new InsufficientBalanceException("Not enough balance to pay");
        }

        bankCard.setAccountSum(bankCard.getAccountSum().subtract(paymentForProcessingDto.getSum()));
        bankCardRepository.save(bankCard);
    }

    public void processRefund(RefundExternalDto refundExternalDto) {
        logger.info("checking card is exists");
        BankCard bankCard = bankCardRepository.findBankCardByCardNumber(refundExternalDto
                .getCardNumber()).orElseThrow(() -> new BankCardNotFoundException("Such Bank card number not found - "
                + refundExternalDto.getCardNumber()));

        bankCard.setAccountSum(bankCard.getAccountSum().add(refundExternalDto.getAmount()));
        bankCardRepository.save(bankCard);
    }

    public boolean checkCardDetailsAreMatch(PaymentForProcessingDto paymentForProcessingDto, BankCard bankCard) {
        return paymentForProcessingDto.getCardNumber().equals(bankCard.getCardNumber()) &&
                paymentForProcessingDto.getCardDate().equals(bankCard.getCardDate()) &&
                paymentForProcessingDto.getCvv().equals(bankCard.getCvv())
                && Objects.equals(paymentForProcessingDto.getCardHolder(), bankCard.getCardHolder());
    }

    public boolean checkSumCanBePaid(BankCard bankCard, BigDecimal sumForPaying) {
        return bankCard.getAccountSum().compareTo(sumForPaying) >= 0;
    }
}
