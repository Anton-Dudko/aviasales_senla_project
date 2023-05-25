package com.aviasales.finance.controller;

import com.aviasales.finance.dto.BlockCarDto;
import com.aviasales.finance.dto.FieldsErrorResponse;
import com.aviasales.finance.dto.SimpleErrorResponse;
import com.aviasales.finance.dto.SimpleResponse;
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
@RequestMapping("admin/cards")
public class CardController {
    private final BlockingCardService blockingCardService;

    @Autowired
    public CardController(BlockingCardService blockingCardService) {
        this.blockingCardService = blockingCardService;
    }

    @PostMapping("/block")
    public ResponseEntity<?> blockCard(@Valid @RequestBody BlockCarDto blockCarDto, BindingResult bindingResult) {
        if (!blockCarDto.validateDto()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new
                    SimpleErrorResponse("Only one of param (card number or country code) should be provided"));
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new FieldsErrorResponse(bindingResult));
        }

        if (blockCarDto.getCardNumber() != null) {
                blockingCardService.blockCard(blockCarDto);
                return ResponseEntity.status(HttpStatus.CREATED).body(new SimpleResponse("Card is blocked"));
        } else {
            blockingCardService.blockCountry(blockCarDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new SimpleResponse("Country is blocked"));
        }
    }

    @PostMapping("/unblock")
    public ResponseEntity<?> unblockCard(@Valid @RequestBody BlockCarDto unblockCardDto, BindingResult bindingResult) {
        if (!unblockCardDto.validateDto()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new
                    SimpleErrorResponse("Only one of param (card number or country code) should be provided"));
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new FieldsErrorResponse(bindingResult));
        }

        if (unblockCardDto.getCardNumber() != null) {
                blockingCardService.unblockCard(unblockCardDto);
        } else {
            blockingCardService.unblockCountry(unblockCardDto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new SimpleResponse("Country is removed from blocked list"));
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
