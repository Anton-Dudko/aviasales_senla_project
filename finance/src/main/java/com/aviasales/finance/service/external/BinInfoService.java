package com.aviasales.finance.service.external;

import com.aviasales.finance.dto.CardInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class BinInfoService {
    private final RestTemplate restTemplate;

    @Autowired
    public BinInfoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public CardInfoDto getCardInfoByBinNumber(String bin) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Version", "3");
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://lookup.binlist.net/" + bin);

        ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
                new HttpEntity<>(headers), String.class);


        return new CardInfoDto();
    }
}
