package com.aviasalestickets.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    @NotNull
    private Long id;
    @NotNull
    private Long userId;
}
