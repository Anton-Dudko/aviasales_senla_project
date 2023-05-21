package eu.senla.tripservice.util.validator;

import eu.senla.tripservice.request.TripRequest;
import eu.senla.tripservice.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class TripValidator implements Validator {
    private final TripService tripService;

    @Autowired
    public TripValidator(TripService tripService) {
        this.tripService = tripService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return TripRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        TripRequest tripRequest = (TripRequest) target;
        String departureCity = tripRequest.getDepartureCity();
        String arrivalCity = tripRequest.getArrivalCity();
        if (departureCity == null || arrivalCity == null) {
            return;
        }
        if (tripService.findByDepartureCityAndArrivalCity(departureCity, arrivalCity).isPresent()) {
            errors.reject("100", "Trip " + tripRequest.getDepartureCity() + " - " + tripRequest.getArrivalCity() + " is already exist");
        }
        if (tripRequest.getDepartureCity().equals(tripRequest.getArrivalCity())) {
            errors.reject("101", "Trip " + tripRequest.getDepartureCity() + " - " + tripRequest.getArrivalCity() + " - cities must be different");
        }
    }
}
