package com.aviasales.finance.external.payment.system.service;

import com.aviasales.finance.external.payment.system.dto.BankCardDto;
import com.aviasales.finance.external.payment.system.enity.BankCard;
import com.aviasales.finance.external.payment.system.exceptions.ExternalAccountException;
import com.aviasales.finance.external.payment.system.repository.BankCardRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final BankCardRepository bankCardRepository;

    public AccountService(BankCardRepository bankCardRepository) {
        this.bankCardRepository = bankCardRepository;
    }

    public void createBankCard(BankCard bankCard) {
        if (bankCardRepository.findBankCardByCardNumber(bankCard.getCardNumber()).isPresent()) {
            throw new ExternalAccountException("Such bank card already created");
        }
        bankCardRepository.save(bankCard);
    }
}
