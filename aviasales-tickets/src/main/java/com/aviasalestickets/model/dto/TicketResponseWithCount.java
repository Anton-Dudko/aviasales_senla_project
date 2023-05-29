package com.aviasalestickets.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponseWithCount {
    int count;
    List<TicketResponse> list;
}
