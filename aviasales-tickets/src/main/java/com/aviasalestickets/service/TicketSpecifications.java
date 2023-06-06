package com.aviasalestickets.service;

import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.TicketStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static java.util.Optional.ofNullable;

public class TicketSpecifications {
    public static Specification<Ticket> tripIdLike(Long flightId) {
        return (root, query, criteriaBuilder) ->
                ofNullable(flightId)
                        .map(value -> criteriaBuilder.equal(root.get("flightId"), flightId))
                        .orElseGet(criteriaBuilder::conjunction);
    }

    public static Specification<Ticket> statusEqual(String status) {
        return (root, query, criteriaBuilder) ->
                ofNullable(status)
                        .map(value -> criteriaBuilder.equal(root.get("status"), TicketStatus.valueOf(status)))
                        .orElseGet(criteriaBuilder::conjunction);
    }

    public static Specification<Ticket> userIdEqual(Long userId) {
        return (root, query, criteriaBuilder) ->
                ofNullable(userId)
                        .map(value -> criteriaBuilder.equal(root.get("userId"), userId))
                        .orElseGet(criteriaBuilder::conjunction);
    }

    public static Specification<Ticket> idEqual(Long id) {
        return (root, query, criteriaBuilder) ->
                ofNullable(id)
                        .map(value -> criteriaBuilder.equal(root.get("id"), id))
                        .orElseGet(criteriaBuilder::conjunction);
    }

    public static Specification<Ticket> fioLike(String fio) {
        return (root, query, criteriaBuilder) ->
                ofNullable(fio)
                        .map(value -> criteriaBuilder.equal(root.get("fio"), fio))
                        .orElseGet(criteriaBuilder::conjunction);
    }

//    public static Specification<Ticket> findAllByIds(List<Long> ids) {
//        return (root, query, criteriaBuilder) ->
//                ofNullable(ids)
//                        .map(value -> criteriaBuilder.in(root.get("id")).value(ids))
//                        .orElseGet(criteriaBuilder::conjunction);
//    }
}
