package eu.senla.tripservice.controller;

import eu.senla.tripservice.exeption.trip.TripNotCreatedException;
import eu.senla.tripservice.request.TripRequest;
import eu.senla.tripservice.response.AllTripsResponse;
import eu.senla.tripservice.response.TripResponse;
import eu.senla.tripservice.service.TripService;
import eu.senla.tripservice.util.error.ErrorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/tripservice/trips")
public class TripController {
    private final TripService tripService;

    @Autowired
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid TripRequest tripRequest, BindingResult bindingResult) {
        validate(bindingResult);
        tripService.save(tripRequest);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public TripResponse getTrip(@PathVariable("id") long id) {
        return tripService.findById(id);
    }

    @GetMapping("/all")
    public AllTripsResponse findAll(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return tripService.findAll(pageRequest);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<HttpStatus> updateTrip(@PathVariable("id") long id,
                                                 @Valid @RequestBody TripRequest tripRequest, BindingResult bindingResult) {
        validate(bindingResult);

        tripService.update(id, tripRequest);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteTripById(@PathVariable("id") long id) {
        tripService.deleteById(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private void validate(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = ErrorUtil.returnErrorMessage(bindingResult);
            throw new TripNotCreatedException(errorMessage);
        }
    }
}
