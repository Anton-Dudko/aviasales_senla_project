package eu.senla.tripservice.controller;

import eu.senla.tripservice.exeption.ErrorResponse;
import eu.senla.tripservice.exeption.flight.FlightNotCreatedException;
import eu.senla.tripservice.exeption.airplane.AirplaneNotFoundException;
import eu.senla.tripservice.exeption.trip.TripAlreadyExistException;
import eu.senla.tripservice.exeption.trip.TripNotCreatedException;
import eu.senla.tripservice.exeption.trip.TripNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(TripNotCreatedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleTripNotCreatedException(TripNotCreatedException tripNotCreatedException) {
        log.error(tripNotCreatedException.getMessage());
        return new ErrorResponse(tripNotCreatedException.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(TripNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleTripNotFoundException(TripNotFoundException tripNotFoundException) {
        log.error(tripNotFoundException.getMessage());
        return new ErrorResponse(tripNotFoundException.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(TripAlreadyExistException.class)
    @ResponseStatus(HttpStatus.ALREADY_REPORTED)
    @ResponseBody
    public ErrorResponse handleTripAlreadyExistException(TripAlreadyExistException tripAlreadyExistException) {
        log.error(tripAlreadyExistException.getMessage());
        return new ErrorResponse(tripAlreadyExistException.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(AirplaneNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleAirplaneNotFoundException(AirplaneNotFoundException airplaneNotFoundException) {
        log.error(airplaneNotFoundException.getMessage());
        return new ErrorResponse(airplaneNotFoundException.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(FlightNotCreatedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleAirplaneNException(FlightNotCreatedException flightNotCreatedException) {
        log.error(flightNotCreatedException.getMessage());
        return new ErrorResponse(flightNotCreatedException.getMessage(), LocalDateTime.now());
    }
}
