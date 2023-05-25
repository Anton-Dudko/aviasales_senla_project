package com.aviasalestickets.service;

import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.TicketStatus;
import org.springframework.data.jpa.domain.Specification;

import static java.util.Optional.ofNullable;

public class TicketSpecifications {
    public static Specification<Ticket> tripIdLike(Long tripId) {
        return (root, query, criteriaBuilder) ->
                ofNullable(tripId)
                        .map(value -> criteriaBuilder.equal(root.get("tripId"), tripId))
                        .orElseGet(criteriaBuilder::conjunction);
    }

    public static Specification<Ticket> statusEqual(String status) {
        return (root, query, criteriaBuilder) ->
                ofNullable(status)
                        .map(value -> criteriaBuilder.equal(root.get("status"), TicketStatus.valueOf(status)))
                        .orElseGet(criteriaBuilder::conjunction);
    }

    public static Specification<Ticket> idEqual(Long userId) {
        return (root, query, criteriaBuilder) ->
                ofNullable(userId)
                        .map(value -> criteriaBuilder.equal(root.get("userId"), userId))
                        .orElseGet(criteriaBuilder::conjunction);
    }
}
