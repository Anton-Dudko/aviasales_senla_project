package eu.senla.aviasales.controller;

import eu.senla.aviasales.request.CustomEmailRequest;
import eu.senla.aviasales.response.SendResponse;
import eu.senla.aviasales.service.SendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@RestController
public class NotificationController {
    private final SendService sendService;

    @PostMapping("/send")
    public SendResponse sendEmail(@RequestBody @Valid CustomEmailRequest customEmailRequest) {
        log.info("Method sendEmail ");
        return sendService.sendCustomEmail(customEmailRequest);
    }
}
