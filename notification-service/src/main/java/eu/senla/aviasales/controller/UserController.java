package eu.senla.aviasales.controller;

import static eu.senla.aviasales.service.listener.constant.Topic.USER_REGISTERED_EVENT;
import eu.senla.aviasales.dto.UserRegistrationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class UserController {
    private final KafkaTemplate<String, UserRegistrationDto> kafkaProducer;

    @PostMapping("/new-user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void userRegisteredEvent(@RequestBody UserRegistrationDto userRegistrationDto) {
        kafkaProducer.send(USER_REGISTERED_EVENT, userRegistrationDto);
    }
}
