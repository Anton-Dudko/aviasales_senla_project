package com.aviasales.finance.external.payment.system.service;

import com.aviasales.finance.external.payment.system.converter.BankCardMapper;
import com.aviasales.finance.external.payment.system.dto.PaymentForProcessingDto;
import com.aviasales.finance.external.payment.system.enity.BankCard;
import com.aviasales.finance.external.payment.system.exceptions.BankCardNotFoundException;
import com.aviasales.finance.external.payment.system.exceptions.CardDetailsMismatchException;
import com.aviasales.finance.external.payment.system.exceptions.InsufficientBalanceException;
import com.aviasales.finance.external.payment.system.repository.BankCardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PaymentProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentProcessingService.class);
    private final BankCardRepository bankCardRepository;
    private final BankCardMapper bankCardMapper;

    @Autowired
    public PaymentProcessingService(BankCardRepository bankCardRepository, BankCardMapper bankCardMapper) {
        this.bankCardRepository = bankCardRepository;
        this.bankCardMapper = bankCardMapper;
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

        bankCard.setAccountSum(bankCard.getAccountSum() - paymentForProcessingDto.getSum());
        bankCardRepository.save(bankCard);
    }

    public boolean checkCardDetailsAreMatch(PaymentForProcessingDto paymentForProcessingDto, BankCard bankCard) {
        return paymentForProcessingDto.getCardNumber().equals(bankCard.getCardNumber()) &&
                paymentForProcessingDto.getCardDate().equals(bankCard.getCardDate()) &&
                paymentForProcessingDto.getCvv().equals(bankCard.getCvv())
                && Objects.equals(paymentForProcessingDto.getCardHolder(), bankCard.getCardHolder());
    }

    public boolean checkSumCanBePaid(BankCard bankCard, double sumForPaying) {
        return bankCard.getAccountSum() >= sumForPaying;
    }
}
