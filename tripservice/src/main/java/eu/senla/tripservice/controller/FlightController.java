package eu.senla.tripservice.controller;

import eu.senla.tripservice.exeption.trip.TripNotCreatedException;
import eu.senla.tripservice.request.FlightRequest;
import eu.senla.tripservice.service.FlightService;
import eu.senla.tripservice.util.error.ErrorUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/tripservice/flights")
public class FlightController {
    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping("/create")
    private ResponseEntity<HttpStatus> create(@RequestBody @Valid FlightRequest flightRequest
                                                , BindingResult bindingResult) {
        validate(bindingResult);
        flightService.save(flightRequest);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    private void validate(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = ErrorUtil.returnErrorMessage(bindingResult);
            throw new TripNotCreatedException(errorMessage);
        }
    }
}
