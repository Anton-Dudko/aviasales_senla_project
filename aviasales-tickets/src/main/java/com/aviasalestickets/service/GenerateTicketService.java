package com.aviasalestickets.service;

import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.dto.request.GenerateTicketRequest;

import java.util.List;

public interface GenerateTicketService {

    List<Ticket> generate(GenerateTicketRequest request);
}
