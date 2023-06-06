package com.aviasales.finance.external.payment.system.controller;

import com.aviasales.finance.dto.FieldsErrorResponse;
import com.aviasales.finance.external.payment.system.converter.BankCardMapper;
import com.aviasales.finance.external.payment.system.dto.BankCardDto;
import com.aviasales.finance.external.payment.system.enity.BankCard;
import com.aviasales.finance.external.payment.system.repository.BankCardRepository;
import com.aviasales.finance.external.payment.system.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/external/account")
public class AccountController {

    private final BankCardRepository bankCardRepository;
    private final BankCardMapper bankCardMapper;
    private final AccountService accountService;

    @Autowired
    public AccountController(BankCardRepository bankCardRepository, BankCardMapper bankCardMapper,
                             AccountService accountService) {
        this.bankCardRepository = bankCardRepository;
        this.bankCardMapper = bankCardMapper;
        this.accountService = accountService;
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
    public ResponseEntity<?> createBankCard(@RequestBody @Valid BankCardDto bankCardDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new FieldsErrorResponse(bindingResult), HttpStatus.BAD_REQUEST);
        }

        accountService.createBankCard(bankCardMapper.convertToEntity(bankCardDto));
        return ResponseEntity.status(HttpStatus.OK).body("Bank card created - " + bankCardDto.getCardNumber());
    }

    @PatchMapping
    public void topUpBalance(@RequestParam String cardNumber, @RequestParam BigDecimal sum) {
        BankCard bankCard = bankCardRepository.findBankCardByCardNumber(cardNumber).get();
        bankCard.setAccountSum(bankCard.getAccountSum().add(sum));
        bankCardRepository.save(bankCard);
    }
}
