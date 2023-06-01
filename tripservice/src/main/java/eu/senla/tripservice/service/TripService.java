package eu.senla.tripservice.service;

import eu.senla.tripservice.entity.Trip;
import eu.senla.tripservice.exeption.trip.TripAlreadyExistsException;
import eu.senla.tripservice.exeption.trip.TripNotFoundException;
import eu.senla.tripservice.mapper.Mapper;
import eu.senla.tripservice.repository.TripRepository;
import eu.senla.tripservice.request.TripRequest;
import eu.senla.tripservice.response.trip.ListTripsFullDataResponse;
import eu.senla.tripservice.response.trip.ListTripsResponse;
import eu.senla.tripservice.response.trip.TripFullDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class TripService {

    private final TripRepository tripRepository;
    private final Mapper mapper;

    @Autowired
    public TripService(TripRepository tripRepository, Mapper mapper) {
        this.tripRepository = tripRepository;
        this.mapper = mapper;
    }

    @Transactional
    public Trip save(TripRequest tripRequest) {
        log.info("TripService-save");
        Trip tripToSave = mapper.mapTripRequestToTrip(tripRequest);

        if (isTripExist(tripToSave.getDepartureCity(), tripToSave.getArrivalCity()))
            throw new TripAlreadyExistsException("Trip " + tripToSave.getDepartureCity()
                    + " - " + tripToSave.getArrivalCity() + " is already exist");

        tripRepository.save(tripToSave);
        log.info("New trip was added: " + tripToSave);
        return tripToSave;
    }

    public Trip findByDepartureCityAndArrivalCity(String departureCity, String arivalCity) {
        return tripRepository.findByDepartureCityAndArrivalCity(departureCity, arivalCity).orElseThrow(() ->
                new TripNotFoundException("Trip " + departureCity + " - " + arivalCity + " not found"));
    }

    public TripFullDataResponse findById(long id) {
        return mapper.mapTripToTripFullDataResponse(findTripById(id));
    }

    public Trip findTripById(long id) {
        log.info("TripService-findTripById: " + id);
        return tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException("Trip with id = " + id + " not found"));
    }

    public ListTripsFullDataResponse findAllTrips(PageRequest pageRequest) {
        log.info("TripService-findAll");
        ListTripsFullDataResponse listTripsFullDataResponse = new ListTripsFullDataResponse();
        listTripsFullDataResponse.setTripFullDataResponseList(tripRepository.findAll(pageRequest)
                .stream()
                .map(mapper::mapTripToTripFullDataResponse)
                .collect(Collectors.toList()));
        listTripsFullDataResponse.setTotal(listTripsFullDataResponse.getTripFullDataResponseList().size());
        return listTripsFullDataResponse;
    }

    public ListTripsResponse findAll(PageRequest pageRequest) {
        log.info("TripService-findAll");
        ListTripsResponse listTripsResponse = new ListTripsResponse();
        listTripsResponse.setTripFullDataResponseList(tripRepository.findAll(pageRequest)
                .stream()
                .map(mapper::mapTripToTripResponse)
                .collect(Collectors.toList()));
        listTripsResponse.setTotal(tripRepository.count());
        return listTripsResponse;
    }

    @Transactional
    public Trip update(long id, TripRequest tripRequest) {
        log.info("TripService-update");
        if (isTripExist(tripRequest.getDepartureCity(), tripRequest.getArrivalCity()))
            throw new TripAlreadyExistsException("Trip " + tripRequest.getDepartureCity()
                    + " - " + tripRequest.getArrivalCity() + " is already exist");

        Trip tripToUpdate = findTripById(id);
        tripToUpdate.setDepartureCity(tripRequest.getDepartureCity());
        tripToUpdate.setArrivalCity(tripRequest.getArrivalCity());
        tripRepository.save(tripToUpdate);

        log.info("Updated trip was saved, new values: " + tripToUpdate);
        return tripToUpdate;
    }

    @Transactional
    public Trip delete(long id) {
        log.info("TripService-deleteById: " + id);
        Trip trip = findTripById(id);
        tripRepository.deleteById(id);
        log.info("Trip with id: " + id + " was deleted");
        return trip;
    }

    private boolean isTripExist(String departureCity, String arrivalCity) {
        return tripRepository.findByDepartureCityAndArrivalCity(departureCity, arrivalCity).isPresent();
    }
}
