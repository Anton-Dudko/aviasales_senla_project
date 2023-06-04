package eu.senla.aviasales.controller;

import eu.senla.aviasales.model.dto.CustomEmailDto;
import eu.senla.aviasales.service.impl.SendToKafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@RestController
public class NotificationController {
    private final SendToKafkaService sendService;

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody @Valid CustomEmailDto customEmailDto) {
        sendService.sendCustomEmail(customEmailDto);
        return ResponseEntity.ok("Email sent");
    }
}
