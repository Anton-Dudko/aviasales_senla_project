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

import javax.validation.constraints.NotNull;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class TripService {
    private final TripRepository tripRepository;
    private final Mapper mapper;

    @Autowired
    public TripService(TripRepository tripRepository,
                       Mapper mapper) {
        this.tripRepository = tripRepository;
        this.mapper = mapper;
    }

    @Transactional
    public Trip save(@NotNull TripRequest tripRequest) {
        log.info("TripService-save");
        Trip tripToSave = mapper.mapTripRequestToTrip(tripRequest);

        if (isTripExist(tripToSave.getDepartureCity(), tripToSave.getArrivalCity()))
            throw new TripAlreadyExistsException("Trip " + tripToSave.getDepartureCity()
                    + " - " + tripToSave.getArrivalCity() + " is already exist");

        tripRepository.save(tripToSave);
        log.info("New trip was added: " + tripToSave);
        return tripToSave;
    }

    public Trip findByDepartureCityAndArrivalCity(@NotNull String departureCity,
                                                  @NotNull String arrivalCity) {
        return tripRepository.findByDepartureCityAndArrivalCity(departureCity, arrivalCity).orElseThrow(() ->
                new TripNotFoundException("Trip " + departureCity + " - " + arrivalCity + " not found"));
    }

    public TripFullDataResponse findById(@NotNull Long id) {
        return mapper.mapTripToTripFullDataResponse(findTripById(id));
    }

    public Trip findTripById(@NotNull Long id) {
        log.info("TripService-findTripById: " + id);
        return tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException("Trip with id = " + id + " not found"));
    }

    public ListTripsFullDataResponse findAllTrips(@NotNull PageRequest pageRequest) {
        log.info("TripService-findAll");
        ListTripsFullDataResponse listTripsFullDataResponse = new ListTripsFullDataResponse();
        listTripsFullDataResponse.setTripFullDataResponseList(tripRepository.findAll(pageRequest)
                .stream()
                .map(mapper::mapTripToTripFullDataResponse)
                .collect(Collectors.toList()));
        listTripsFullDataResponse.setTotal(listTripsFullDataResponse.getTripFullDataResponseList().size());
        return listTripsFullDataResponse;
    }

    public ListTripsResponse findAll(@NotNull PageRequest pageRequest) {
        log.info("TripService-findAll");
        ListTripsResponse listTripsResponse = new ListTripsResponse();
        listTripsResponse.setTripFullDataResponseList(tripRepository.findAll(pageRequest)
                .stream()
                .map(mapper::mapTripToTripResponse)
                .collect(Collectors.toList()));
        listTripsResponse.setTotal(listTripsResponse.getTripFullDataResponseList().size());
        return listTripsResponse;
    }

    @Transactional
    public Trip update(@NotNull Long id,
                       @NotNull TripRequest tripRequest) {
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
    public TripFullDataResponse delete(@NotNull Long id) {
        log.info("TripService-deleteById: " + id);
        Trip trip = findTripById(id);
        tripRepository.deleteById(id);
        log.info("Trip with id: " + id + " was deleted");
        TripFullDataResponse response = mapper.mapTripToTripFullDataResponse(trip);
        response.setMessage("Trip was deleted");
        return response;
    }

    private boolean isTripExist(@NotNull String departureCity,
                                @NotNull String arrivalCity) {
        return tripRepository.findByDepartureCityAndArrivalCity(departureCity, arrivalCity).isPresent();
    }
}
