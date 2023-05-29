package eu.senla.tripservice.controller;

import eu.senla.tripservice.entity.Flight;
import eu.senla.tripservice.exeption.flight.FlightValidationException;
import eu.senla.tripservice.request.FindFlightRequest;
import eu.senla.tripservice.request.FlightRequest;
import eu.senla.tripservice.response.flight.FlightFullDataResponse;
import eu.senla.tripservice.response.flight.FlightInfo;
import eu.senla.tripservice.response.flight.ListFlightsFullDataResponse;
import eu.senla.tripservice.response.flight.ListFlightsResponse;
import eu.senla.tripservice.service.FlightService;
import eu.senla.tripservice.util.error.ErrorUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Flight> create(@RequestBody @Valid FlightRequest flightRequest,
                                         BindingResult bindingResult) {
        validate(bindingResult);
        return ResponseEntity.ok(flightService.create(flightRequest));
    }

    @GetMapping("/admin/find/{id}")
    public FlightFullDataResponse findById(@PathVariable("id") long id) {
        return flightService.findById(id);
    }

    @GetMapping("/admin/find/all")
    public ListFlightsFullDataResponse findAll(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return flightService.findAll(pageRequest);
    }

    @GetMapping("/guest/find")
    public ListFlightsResponse findBySpec(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size,
                                          @RequestBody @Valid FindFlightRequest flightRequest,
                                          BindingResult bindingResult) {
        validate(bindingResult);
        Pageable pageable = PageRequest.of(page, size);
        return flightService.find(flightRequest, pageable);
    }

    @GetMapping("/guest/info/{id}")
    public FlightInfo showInfo(@PathVariable("id") long id) {
        return flightService.info(id);
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<Flight> update(@PathVariable("id") long id,
                                         @Valid @RequestBody FlightRequest flightRequest,
                                         BindingResult bindingResult) {
        validate(bindingResult);
        return ResponseEntity.ok(flightService.update(id, flightRequest));
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") long id) {
        flightService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private void validate(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = ErrorUtil.returnErrorMessage(bindingResult);
            throw new FlightValidationException(errorMessage);
        }
    }
}
