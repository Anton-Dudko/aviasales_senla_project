package com.aviasalestickets.service;

import com.aviasalestickets.model.Ticket;
import org.springframework.data.jpa.domain.Specification;

import static java.util.Optional.ofNullable;

public class TicketSpecifications {
    public static Specification<Ticket> tripIdLike(Long tripId) {
        return (root, query, criteriaBuilder) ->
                ofNullable(tripId)
                        .map(value -> criteriaBuilder.equal(root.get("tripId"), tripId))
                        .orElseGet(criteriaBuilder::conjunction);
    }

    public static Specification<Ticket> statusLike(String status) {
        return (root, query, criteriaBuilder) ->
                ofNullable(status)
                        .map(value -> criteriaBuilder.like(root.get("status"), status))
                        .orElseGet(criteriaBuilder::conjunction);
    }

    public static Specification<Ticket> idEqual(Long userId) {
        return (root, query, criteriaBuilder) ->
                ofNullable(userId)
                        .map(value -> criteriaBuilder.equal(root.get("userId"), userId))
                        .orElseGet(criteriaBuilder::conjunction);
    }
}
