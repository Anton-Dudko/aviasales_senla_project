package com.aviasales.finance.service.external;

import com.aviasales.finance.dto.TicketInfoDto;
import com.aviasales.finance.dto.UserDetailsDto;
import com.aviasales.finance.enums.TicketStatus;
import com.aviasales.finance.enums.UserRole;
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
    private final String tickerServiceUrl = "http://ticket-service:8088/tickets";

    @Autowired
    public TicketService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<TicketInfoDto> getTicketInfo(List<Long> ticketList) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(tickerServiceUrl)
                .pathSegment("findByIds")
                .queryParam("ids", ticketList.stream().map(Object::toString).collect(Collectors.joining(",")));

        try {
            ResponseEntity<List<TicketInfoDto>> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()), new ParameterizedTypeReference<List<TicketInfoDto>>() {
                    });
            return Optional.ofNullable(response.getBody()).orElseThrow(() -> new TicketServiceException("Such tickets not found"));

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error received from ticket service: " + e.getMessage(), e);
            throw new TicketServiceException(e.getMessage());
        }

//        TicketInfoDto ticketInfoDto = new TicketInfoDto();
//        ticketInfoDto.setId("1");
//        ticketInfoDto.setPrice(new BigDecimal(100));
//        ticketInfoDto.setStatus(TicketStatus.FREE);
//
//        TicketInfoDto ticked2nd = new TicketInfoDto();
//        ticked2nd.setId("2");
//        ticked2nd.setPrice(new BigDecimal(250));
//        ticked2nd.setStatus(TicketStatus.FREE);
//
//        return List.of(ticketInfoDto, ticked2nd);
    }

    public List<TicketInfoDto> getTicketInfoForPaying(List<Long> ticketList, UserDetailsDto userDetailsDto) {
        List<TicketInfoDto> ticketInfoDtos = getTicketInfo(ticketList);
        if (ticketInfoDtos.size() != ticketList.size()) {
            throw new TicketServiceException("Not all tickets received from ticket service");
        }

        logger.info("Checking ticket status valid");
        ticketInfoDtos.forEach(this::checkTicketNotPaid);

        if (userDetailsDto.getRole().equals(UserRole.ROLE_USER)) {
            logger.info("Checking booked tickets user id match");
            ticketInfoDtos.stream().filter(ticket -> ticket.getStatus().equals(TicketStatus.BOOKED))
                    .forEach(x -> checkTicketUserMatch(x, userDetailsDto.getUserId()));
        }

        return ticketInfoDtos;
    }

    public void updateTicketToPaid(List<Long> ticketList) {
        logger.info("Update tickets to Paid");
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(tickerServiceUrl)
                .pathSegment("pay-tickets").queryParam("ticketsId",
                        ticketList.stream().map(Object::toString).collect(Collectors.joining(",")));
        try {
            ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()), String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error received from ticket service during updating to paid status: " + e.getMessage(), e);
            throw new TicketServiceException(e.getMessage());
        }
    }

    public void updateTicketToRefund(List<Long> ticketList) {
        logger.info("Update tickets to free");
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(tickerServiceUrl)
                .pathSegment("refund").queryParam("ticketsId",
                        ticketList.stream().map(Object::toString).collect(Collectors.joining(",")));
        try {
            ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST,
                    new HttpEntity<>(new HttpHeaders()), String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error received from ticket service during updating to Free status: " + e.getMessage(), e);
            throw new TicketServiceException(e.getMessage());
        }
    }

    public void checkTicketNotPaid(TicketInfoDto ticketInfoDto) {
        logger.info("Checking ticked status not paid");
        if (ticketInfoDto.getStatus().equals(TicketStatus.PAID)) {
            throw new TicketServiceException("Ticket already paid, ticket id - " + ticketInfoDto.getId());
        }
    }

    public void checkTicketUserMatch(TicketInfoDto ticketInfoDto, long userId) {
        logger.info("Checking ticked user match");
        if (ticketInfoDto.getUserId() != userId) {
            throw new TicketServiceException("Ticket was booked by other user, ticket id - " + ticketInfoDto.getId() +
                    ". Your user id - " + userId);
        }
    }
}
