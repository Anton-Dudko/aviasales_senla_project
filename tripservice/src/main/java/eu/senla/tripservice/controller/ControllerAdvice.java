package eu.senla.tripservice.controller;

import eu.senla.tripservice.exeption.ErrorResponse;
import eu.senla.tripservice.exeption.ParseException;
import eu.senla.tripservice.exeption.airplane.AirplaneNotFoundException;
import eu.senla.tripservice.exeption.flight.FlightAlreadyExistsException;
import eu.senla.tripservice.exeption.flight.FlightNotFoundException;
import eu.senla.tripservice.exeption.flight.FlightValidationException;
import eu.senla.tripservice.exeption.subscription.SubscriptionAlreadyExistsException;
import eu.senla.tripservice.exeption.subscription.SubscriptionException;
import eu.senla.tripservice.exeption.ticket.TicketsRequestException;
import eu.senla.tripservice.exeption.trip.TripAlreadyExistsException;
import eu.senla.tripservice.exeption.trip.TripNotCreatedException;
import eu.senla.tripservice.exeption.trip.TripNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
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

    @ExceptionHandler(TripAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.ALREADY_REPORTED)
    @ResponseBody
    public ErrorResponse handleTripAlreadyExistsException(TripAlreadyExistsException tripAlreadyExistsException) {
        log.error(tripAlreadyExistsException.getMessage());
        return new ErrorResponse(tripAlreadyExistsException.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(AirplaneNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleAirplaneNotFoundException(AirplaneNotFoundException airplaneNotFoundException) {
        log.error(airplaneNotFoundException.getMessage());
        return new ErrorResponse(airplaneNotFoundException.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(FlightValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleFlightNotCreatedException(FlightValidationException flightValidationException) {
        log.error(flightValidationException.getMessage());
        return new ErrorResponse(flightValidationException.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(FlightNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleFlightNotFoundException(FlightNotFoundException flightNotFoundException) {
        log.error(flightNotFoundException.getMessage());
        return new ErrorResponse(flightNotFoundException.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(FlightAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.ALREADY_REPORTED)
    @ResponseBody
    public ErrorResponse handleFlightAlreadyExistsException(FlightAlreadyExistsException flightAlreadyExistsException) {
        log.error(flightAlreadyExistsException.getMessage());
        return new ErrorResponse(flightAlreadyExistsException.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(TicketsRequestException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleTicketsNotCreatedException(TicketsRequestException ticketsRequestException) {
        log.error(ticketsRequestException.getMessage());
        return new ErrorResponse(ticketsRequestException.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(ParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleParseException(ParseException parseException) {
        log.error(parseException.getMessage());
        return new ErrorResponse(parseException.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(SubscriptionAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.ALREADY_REPORTED)
    @ResponseBody
    public ErrorResponse handleSubscriptionAlreadyExistsException(SubscriptionAlreadyExistsException subscriptionAlreadyExistsException) {
        log.error(subscriptionAlreadyExistsException.getMessage());
        return new ErrorResponse(subscriptionAlreadyExistsException.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(SubscriptionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleSubscriptionException(SubscriptionException subscriptionException) {
        log.error(subscriptionException.getMessage());
        return new ErrorResponse(subscriptionException.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMissingRequestHeaderException(MissingRequestHeaderException missingRequestHeaderException) {
        log.error(missingRequestHeaderException.getMessage());
        return new ErrorResponse(missingRequestHeaderException.getMessage(), LocalDateTime.now());
    }
}
