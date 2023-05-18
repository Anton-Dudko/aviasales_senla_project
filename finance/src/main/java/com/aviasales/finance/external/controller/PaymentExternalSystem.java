package com.aviasales.finance.external.controller;

import com.aviasales.finance.dto.CardInfoDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentExternalSystem {

    @GetMapping
    public CardInfoDto getCardInfoByBin(String bin) {
        return new CardInfoDto();
    }
}
