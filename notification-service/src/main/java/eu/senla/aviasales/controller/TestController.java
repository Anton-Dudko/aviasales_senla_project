package eu.senla.aviasales.controller;

import static eu.senla.aviasales.model.constant.Topic.CANCELED_TICKET_RESERVATION_EVENT;
import static eu.senla.aviasales.model.constant.Topic.NEW_TICKET_RESERVATION_EVENT;
import static eu.senla.aviasales.model.constant.Topic.NEW_TRIP_EVENT;
import static eu.senla.aviasales.model.constant.Topic.PAYMENT_ERROR_EVENT;
import static eu.senla.aviasales.model.constant.Topic.PAYMENT_SUCCESS_EVENT;
import static eu.senla.aviasales.model.constant.Topic.TRIP_CANCELED_EVENT;
import static eu.senla.aviasales.model.constant.Topic.USER_REGISTERED_EVENT;
import static eu.senla.aviasales.model.constant.Topic.USER_RESET_PASSWORD_EVENT;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
// TODO TO BE DELETED!

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class TestController {
    private final KafkaTemplate<String, Map<String, Object>> usReg;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/payment-success")
    public void testPaymentSuccess(@RequestBody Map<String, Object> dto) {
        usReg.send(PAYMENT_SUCCESS_EVENT, dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/payment-error")
    public void testPaymentError(@RequestBody Map<String, Object> dto) {
        usReg.send(PAYMENT_ERROR_EVENT, dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/us-reg")
    public void testUserReg(@RequestBody Map<String, Object> dto) {
        usReg.send(USER_REGISTERED_EVENT, dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/us-reset")
    public void testUserReset(@RequestBody Map<String, Object> dto) {
        usReg.send(USER_RESET_PASSWORD_EVENT, dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/tick-canc")
    public void testTickCanc(@RequestBody Map<String, Object> dto) {
        usReg.send(CANCELED_TICKET_RESERVATION_EVENT, dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/new-ticket")
    public void testNewTick(@RequestBody Map<String, Object> dto) {
        usReg.send(NEW_TICKET_RESERVATION_EVENT, dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/new-trip")
    public void testNewTrip(@RequestBody Map<String, Object> dto) {
        usReg.send(NEW_TRIP_EVENT, dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/pay-error")
    public void testPayError(@RequestBody Map<String, Object> dto) {
        usReg.send(PAYMENT_ERROR_EVENT, dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/pay-suc")
    public void testPaySuc(@RequestBody Map<String, Object> dto) {
        usReg.send(PAYMENT_SUCCESS_EVENT, dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/trip-canc")
    public void testTripCanc(@RequestBody Map<String, Object> dto) {
        usReg.send(TRIP_CANCELED_EVENT, dto);
    }

}
