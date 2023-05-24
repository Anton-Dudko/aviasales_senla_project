package eu.senla.tripservice.controller;

import eu.senla.tripservice.exeption.trip.TripNotCreatedException;
import eu.senla.tripservice.request.TripRequest;
import eu.senla.tripservice.response.trip.ListTripsFullDataResponse;
import eu.senla.tripservice.response.trip.ListTripsResponse;
import eu.senla.tripservice.response.trip.TripFullDataResponse;
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
@RequestMapping("/trips")
public class TripController {
    private final TripService tripService;

    @Autowired
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping("/admin/create")
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid TripRequest tripRequest, BindingResult bindingResult) {
        validate(bindingResult);
        tripService.save(tripRequest);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/admin/find/{id}")
    public TripFullDataResponse findById(@PathVariable("id") long id) {
        return tripService.findById(id);
    }

    @GetMapping("/admin/all")
    public ListTripsFullDataResponse findAllTrips(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return tripService.findAllTrips(pageRequest);
    }

    @GetMapping("/guest/all")
    public ListTripsResponse findAll(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return tripService.findAll(pageRequest);
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<HttpStatus> update(@PathVariable("id") long id,
                                             @Valid @RequestBody TripRequest tripRequest,
                                             BindingResult bindingResult) {
        validate(bindingResult);
        tripService.update(id, tripRequest);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") long id) {
        tripService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private void validate(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = ErrorUtil.returnErrorMessage(bindingResult);
            throw new TripNotCreatedException(errorMessage);
        }
    }
}
