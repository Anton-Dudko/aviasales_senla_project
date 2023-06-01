package com.aviasales.finance.external.payment.system.controller;

import com.aviasales.finance.external.payment.system.converter.BankCardMapper;
import com.aviasales.finance.external.payment.system.dto.BankCardDto;
import com.aviasales.finance.external.payment.system.enity.BankCard;
import com.aviasales.finance.external.payment.system.repository.BankCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/external/account")
public class AccountController {

    private final BankCardRepository bankCardRepository;
    private final BankCardMapper bankCardMapper;

    @Autowired
    public AccountController(BankCardRepository bankCardRepository, BankCardMapper bankCardMapper) {
        this.bankCardRepository = bankCardRepository;
        this.bankCardMapper = bankCardMapper;
    }

    @GetMapping("/info")
    public BankCard getCardInfo(@RequestParam String cardNumber) {
        return bankCardRepository.findBankCardByCardNumber(cardNumber).get();
    }

    @GetMapping("/available-cards")
    public List<BankCard> getAvailableCards() {
        return bankCardRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createBankCard(@RequestBody BankCardDto bankCardDto) {
        bankCardRepository.save(bankCardMapper.convertToEntity(bankCardDto));
        return ResponseEntity.status(HttpStatus.OK).body("Bank card created - " + bankCardDto.getCardNumber());
    }

    @PatchMapping
    public void topUpBalance(@RequestParam String cardNumber, @RequestParam double sum) {
        BankCard bankCard = bankCardRepository.findBankCardByCardNumber(cardNumber).get();
        bankCard.setAccountSum(bankCard.getAccountSum() + sum);
        bankCardRepository.save(bankCard);
    }
}
