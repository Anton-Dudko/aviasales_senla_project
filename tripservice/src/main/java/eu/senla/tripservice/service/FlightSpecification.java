package eu.senla.tripservice.service;

import eu.senla.tripservice.entity.Flight;
import eu.senla.tripservice.request.FindFlightRequest;
import eu.senla.tripservice.util.time.TimeFormatter;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class FlightSpecification implements Specification<Flight> {

    private FindFlightRequest request;

    @Override
    public Predicate toPredicate(Root<Flight> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (request.getDepartureCity() != null && !request.getDepartureCity().isEmpty()) {
            predicates.add(criteriaBuilder.equal(root.get("trip").get("departureCity"), request.getDepartureCity()));
        }
        if (request.getArrivalCity() != null && !request.getArrivalCity().isEmpty()) {
            predicates.add(criteriaBuilder.equal(root.get("trip").get("arrivalCity"), request.getArrivalCity()));
        }
        if (request.getDateFrom() != null && !request.getDateFrom().isEmpty()) {
            LocalDateTime departureDate = TimeFormatter.formatStringToDateTime(request.getDateFrom());
            predicates.add(criteriaBuilder.between(root.get("departureDateTime"), departureDate, departureDate.plusDays(1)));
        }
        if (request.getMinSeatCount() != 0) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("airplane").get("numberOfSeats"), request.getMinSeatCount()));
        }
        if (request.getMaxSeatCount() != 0) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("airplane").get("numberOfSeats"), request.getMaxSeatCount()));
        }
        if (request.isFirstClass()) {
            predicates.add(criteriaBuilder.equal(root.get("airplane").get("hasFirstClass"), request.isFirstClass()));
        }
        if (request.isSortByDuration()) {
            query.orderBy(criteriaBuilder.asc(root.get("duration")));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

    }
}
