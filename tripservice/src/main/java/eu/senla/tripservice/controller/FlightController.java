package eu.senla.tripservice.controller;

import eu.senla.tripservice.exeption.trip.TripNotCreatedException;
import eu.senla.tripservice.request.FindFlightRequest;
import eu.senla.tripservice.request.FlightRequest;
import eu.senla.tripservice.response.flight.*;
import eu.senla.tripservice.service.FlightService;
import eu.senla.tripservice.util.error.ErrorUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/flights")
public class FlightController {
    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping("/admin/create")
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid FlightRequest flightRequest,
                                             BindingResult bindingResult) {
        validate(bindingResult);
        flightService.create(flightRequest);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/admin/{id}")
    public FlightFullDataResponse findById(@PathVariable("id") long id) {
        return flightService.findById(id);
    }

    @GetMapping("/admin/all")
    public ListFlightsFullDataResponse findAll(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return flightService.findAll(pageRequest);
    }

    @GetMapping("/guest/find")
    public ListFlightsResponse findByTripAndDate(@RequestBody FindFlightRequest flightRequest) {
        return flightService.find(flightRequest);
    }

    @GetMapping("/guest/info/{id}")
    public FlightInfo showInfo(@PathVariable("id") long id) {
        return flightService.info(id);
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<HttpStatus> update(@PathVariable("id") long id,
                                             @Valid @RequestBody FlightRequest flightRequest,
                                             BindingResult bindingResult) {
        validate(bindingResult);
        flightService.update(id, flightRequest);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") long id) {
        flightService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    private void validate(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = ErrorUtil.returnErrorMessage(bindingResult);
            throw new TripNotCreatedException(errorMessage);
        }
    }
}
