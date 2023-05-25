package com.aviasales.finance.service.external;

import com.aviasales.finance.dto.TicketInfoDto;
import com.aviasales.finance.enums.TicketStatus;
import com.aviasales.finance.exception.TickedAlreadyPaidException;
import com.aviasales.finance.exception.TicketNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
public class TicketService {
    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);
    private final RestTemplate restTemplate;
    private String tickerServiceUrl = "/";

    @Autowired
    public TicketService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public TicketInfoDto getTicketInfo(String tickedId) {
        //ToDo implement ticket service calling
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(tickerServiceUrl).queryParam("ticketId",
                tickedId);

//        ResponseEntity<TicketInfoDto> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
//                new HttpEntity<>(new Headers()), TicketInfoDto.class);

        TicketInfoDto ticketInfoDto = new TicketInfoDto();
        ticketInfoDto.setTicketId(tickedId);
        ticketInfoDto.setPrice(100);
        ticketInfoDto.setTicketStatus(TicketStatus.AVAILABLE);

        return Optional.ofNullable(ticketInfoDto).orElseThrow(() -> new TicketNotFoundException(tickedId));
    }

    public void updateTicketToPaid(String tickedId) {
        logger.info("Update ticket to Paid");
        //ToDo implement ticket service corrections, should be set status Paid
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(tickerServiceUrl).queryParam("ticketId",
                tickedId);
//        ResponseEntity<TicketInfoDto> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
//                new HttpEntity<>(new Headers()), TicketInfoDto.class);
    }

    public void checkTicketNotPaid(TicketInfoDto ticketInfoDto) {
        logger.info("Checking ticked status not paid");
        if (ticketInfoDto.getTicketStatus().equals(TicketStatus.PAID)) {
            throw new TickedAlreadyPaidException(ticketInfoDto.getTicketId());
        }
    }
}
