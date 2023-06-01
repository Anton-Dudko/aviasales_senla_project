package com.aviasales.finance.service.external;

import com.aviasales.finance.dto.TicketInfoDto;
import com.aviasales.finance.enums.TicketStatus;
import com.aviasales.finance.exception.TicketServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketService {
    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);
    private final RestTemplate restTemplate;
    private final String tickerServiceUrl = "http://ticket-service:8080/tickets";

    @Autowired
    public TicketService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<TicketInfoDto> getTicketInfo(List<Long> ticketList) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(tickerServiceUrl).
                queryParam("ids",
                        ticketList.stream().map(Object::toString).collect(Collectors.joining(",")));

        try {
            ResponseEntity<List<TicketInfoDto>> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()), new ParameterizedTypeReference<List<TicketInfoDto>>() {});
            return Optional.ofNullable(response.getBody()).orElseThrow(() -> new TicketServiceException("Such tickets not found"));

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error received from ticket service: " + e.getMessage(), e);
            throw new TicketServiceException(e.getMessage());
        }

//        TicketInfoDto ticketInfoDto = new TicketInfoDto();
//        ticketInfoDto.setId(tickedId);
//        ticketInfoDto.setPrice(new BigDecimal(100));
//        ticketInfoDto.setStatus(TicketStatus.FREE);

    }

    public List<TicketInfoDto> getTicketInfoForPaying(List<Long> ticketList) {
        //ToDo decide if it will be part of ticket service functionality
        List<TicketInfoDto> ticketInfoDtos = getTicketInfo(ticketList);
        if (ticketInfoDtos.size() != ticketList.size()) {
            throw new TicketServiceException("Not all tickets received from ticket service");
        }

        ticketInfoDtos.forEach(this::checkTicketNotPaid);

        return ticketInfoDtos;
    }

    public void updateTicketToPaid(List<Long> ticketList) {
        logger.info("Update tickets to Paid");
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(tickerServiceUrl)
                .pathSegment("pay").queryParam("ids",
                        ticketList.stream().map(Object::toString).collect(Collectors.joining(",")));
        try {
            ResponseEntity<TicketInfoDto> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST,
                    new HttpEntity<>(new HttpHeaders()), TicketInfoDto.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error received from ticket service during updating to paid status: " + e.getMessage(), e);
            throw new TicketServiceException(e.getMessage());
        }
    }

    public void checkTicketNotPaid(TicketInfoDto ticketInfoDto) {
        logger.info("Checking ticked status not paid");
        if (ticketInfoDto.getStatus().equals(TicketStatus.PAID)) {
            throw new TicketServiceException("Ticket already paid, ticket id - " + ticketInfoDto.getId());
        }
    }
}
