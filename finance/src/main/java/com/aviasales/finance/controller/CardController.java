package com.aviasales.finance.controller;

import com.aviasales.finance.dto.BlockCarDto;
import com.aviasales.finance.entity.BlockedCard;
import com.aviasales.finance.entity.BlockedCountry;
import com.aviasales.finance.service.BlockingCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/cards")
public class CardController {
    private final BlockingCardService blockingCardService;

    @Autowired
    public CardController(BlockingCardService blockingCardService) {
        this.blockingCardService = blockingCardService;
    }

    @PostMapping("/block")
    public ResponseEntity<?> blockCard(@Valid @RequestBody BlockCarDto blockCarDto, BindingResult bindingResult) {
        if (blockCarDto == null || (blockCarDto.getCardNumber() == null && blockCarDto.getCountryCode() == null)) {
            return new ResponseEntity<>("One of param (card number or country code) is required", HttpStatus.BAD_REQUEST);
        }

        //ToDo выяснить добавлять ли возможность сетать сразу два варианта в запросе
        if (bindingResult.hasErrors()) {
            // обработка невалидного запроса
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(errorMessage);
        }


        if (blockCarDto.getCardNumber() != null) {
            if (blockingCardService.isCardBlocked(blockCarDto.getCardNumber())) {
                return new ResponseEntity<>("Card is already blocked", HttpStatus.CONFLICT);
            } else {
                blockingCardService.blockCard(blockCarDto);
                return ResponseEntity.status(HttpStatus.CREATED).body("Card is blocked");
            }
        } else {
            if (blockingCardService.isCountryBlocked(blockCarDto.getCountryCode())) {
                return new ResponseEntity<>("This country is already blocked", HttpStatus.CONFLICT);
            } else {
                blockingCardService.blockCountry(blockCarDto);
                return ResponseEntity.status(HttpStatus.CREATED).body("Country is blocked");
            }
        }
    }

    @PostMapping("/unblock")
    public ResponseEntity<?> unblockCard(@Valid @RequestBody BlockCarDto unblockCardDto, BindingResult bindingResult) {
        if (unblockCardDto == null || (unblockCardDto.getCardNumber() == null && unblockCardDto.getCountryCode() == null)) {
            return new ResponseEntity<>("One of param (card number or country code) is required to unblock",
                    HttpStatus.BAD_REQUEST);
        }

        //ToDo выяснить добавлять ли возможность сетать сразу два варианта в запросе
        if (bindingResult.hasErrors()) {
            // обработка невалидного запроса
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(errorMessage);
        }

        if (unblockCardDto.getCardNumber() != null) {
            if (!blockingCardService.isCardBlocked(unblockCardDto.getCardNumber())) {
                return new ResponseEntity<>("Specified card is not blocked", HttpStatus.BAD_REQUEST);
            } else {
                blockingCardService.unblockCard(unblockCardDto);
                return ResponseEntity.status(HttpStatus.OK).body("Card is removed from blocked list");
            }
        } else {
            if (!blockingCardService.isCountryBlocked(unblockCardDto.getCountryCode())) {
                return new ResponseEntity<>("This country is not blocked", HttpStatus.BAD_REQUEST);
            } else {
                blockingCardService.unblockCountry(unblockCardDto);
                return ResponseEntity.status(HttpStatus.OK).body("Country is removed from blocked list");
            }
        }
    }

    @GetMapping("/blocked-cards")
    public List<BlockedCard> getBlockedCards() {
        return blockingCardService.getBlockedCards();
    }

    @GetMapping("/blocked-countries")
    public List<BlockedCountry> getBlockedCountries() {
        return blockingCardService.getBlockedCountries();
    }
}
