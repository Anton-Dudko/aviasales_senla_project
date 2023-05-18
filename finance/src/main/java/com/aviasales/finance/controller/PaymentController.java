package com.aviasales.finance.controller;

import com.aviasales.finance.dto.PaymentDto;
import com.aviasales.finance.external.controller.PaymentExternalSystem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
public class PaymentController {
    private PaymentExternalSystem paymentExternalSystem;

    @PostMapping("/payment")
    public ResponseEntity<?> createPayment(@Valid @RequestBody PaymentDto paymentDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors().stream()
                    .filter(error -> error instanceof FieldError)
                    .map(error -> (FieldError) error)
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)), HttpStatus.BAD_REQUEST);
        }

        //toDo add card fields validataion
        //ToDo Check if ticket exists and available using tickets endpoint
        //ToDo add check that ticket was booked by others person
//        boolean result = ticketService.ch(paymentDto.getTicketId());


//        if (!result) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body("Ticket with the following id not available for paying - " + paymentDto.getTicketId());
//        }



        //Что насчет цены тикета?
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
