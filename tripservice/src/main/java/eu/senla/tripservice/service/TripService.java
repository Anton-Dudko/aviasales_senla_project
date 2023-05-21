package eu.senla.tripservice.service;

import eu.senla.tripservice.entity.Trip;
import eu.senla.tripservice.exeption.trip.TripAlreadyExistException;
import eu.senla.tripservice.exeption.trip.TripNotFoundException;
import eu.senla.tripservice.repository.TripRepository;
import eu.senla.tripservice.request.TripRequest;
import eu.senla.tripservice.response.AllTripsResponse;
import eu.senla.tripservice.response.TripResponse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class TripService {

    private final TripRepository tripRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TripService(TripRepository tripRepository, ModelMapper modelMapper) {
        this.tripRepository = tripRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public void save(TripRequest tripRequest) {
        log.info("TripService-save");
        Trip tripToSave = convertToTrip(tripRequest);

        if (isTripExist(tripToSave.getDepartureCity(), tripToSave.getArrivalCity()))
            throw new TripAlreadyExistException("Trip " + tripToSave.getDepartureCity()
                    + " - " + tripToSave.getArrivalCity() + " is already exist");

        tripRepository.save(convertToTrip(tripRequest));
        log.info("New trip was added: " + tripRequest);
    }

    public Optional<Trip> findByDepartureCityAndArrivalCity(String departureCity, String arivalCity) {
        return tripRepository.findByDepartureCityAndArrivalCity(departureCity, arivalCity);
    }

    public TripResponse findById(long id) {
        return convertToTripResponse(findTripById(id));
    }

    public Trip findTripById(long id) {
        log.info("TripService-findTripById: " + id);
        return tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException("Trip with id = " + id + " not found"));
    }

    public AllTripsResponse findAll(PageRequest pageRequest) {
        log.info("TripService-findAll");
        AllTripsResponse allTripsResponse = new AllTripsResponse();
        allTripsResponse.setTripResponseList(tripRepository.findAll(pageRequest).getContent());
        allTripsResponse.setCount(tripRepository.count());
        return allTripsResponse;
    }

    @Transactional
    public void update(long id, TripRequest tripRequest) {
        log.info("TripService-update");
        if (isTripExist(tripRequest.getDepartureCity(), tripRequest.getArrivalCity()))
            throw new TripAlreadyExistException("Trip " + tripRequest.getDepartureCity()
                    + " - " + tripRequest.getArrivalCity() + " is already exist");

        Trip tripToUpdate = findTripById(id);
        tripToUpdate.setDepartureCity(tripRequest.getDepartureCity());
        tripToUpdate.setArrivalCity(tripRequest.getArrivalCity());
        tripRepository.save(tripToUpdate);

        log.info("Updated trip was saved, new values: " + tripToUpdate);
    }

    @Transactional
    public void deleteById(long id) {
        log.info("TripService-deleteById: " + id);
        findTripById(id);
        tripRepository.deleteById(id);
        log.info("Trip with id: " + id + " was deleted");
    }

    private Trip convertToTrip(TripRequest tripRequest) {
        return modelMapper.map(tripRequest, Trip.class);
    }

    private TripResponse convertToTripResponse(Trip trip) {
        return modelMapper.map(trip, TripResponse.class);
    }

    private boolean isTripExist(String departureCity, String arrivalCity) {
        return tripRepository.findByDepartureCityAndArrivalCity(departureCity, arrivalCity).isPresent();
    }
}
