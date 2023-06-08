package com.aviasales.finance.service.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BinInfoService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(BinInfoService.class);
    @Value("${bin.info.service.url}")
    private String BIN_SERVICE_URL;
    @Value("${bin.info.service.token}")
    private String basicToken;

    @Autowired
    public BinInfoService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Optional<String> getCountryCodeByBinNumber(String bin) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(basicToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("fullBin", bin);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BIN_SERVICE_URL);

        try {
            ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST,
                    new HttpEntity<>(requestBody, headers), String.class);

            try {
                return Optional.of(objectMapper.readTree(response.getBody()).get("countryAlpha2").asText());
            } catch (Exception e) {
                logger.error("Error during country info extract from response", e);
                return Optional.empty();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error received from external BIN service: " + e.getMessage(), e);
            return Optional.empty();
        }

    }
}
