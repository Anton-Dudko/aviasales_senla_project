package com.aviasalestickets.service;

import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.TicketStatus;
import com.aviasalestickets.model.TicketType;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;

import static java.util.Optional.ofNullable;

public class TicketSpecifications {
    public static Specification<Ticket> flightIdEqual(Long flightId) {
        return (root, query, criteriaBuilder) ->
                ofNullable(flightId)
                        .map(value -> criteriaBuilder.equal(root.get("flightId"), flightId))
                        .orElseGet(criteriaBuilder::conjunction);
    }

    public static Specification<Ticket> statusEqual(String status) {
        return (root, query, criteriaBuilder) ->
                ofNullable(status)
                        .filter(element -> Arrays.stream(TicketStatus.values()).anyMatch(el -> StringUtils.equals(el.name(), status)))
                        .map(value -> criteriaBuilder.equal(root.get("status"), TicketStatus.valueOf(status)))
                        .orElseGet(criteriaBuilder::conjunction);
    }

    public static Specification<Ticket> userIdEqual(Long userId) {
        return (root, query, criteriaBuilder) ->
                ofNullable(userId)
                        .map(value -> criteriaBuilder.equal(root.get("userId"), userId))
                        .orElseGet(criteriaBuilder::conjunction);
    }

    public static Specification<Ticket> ticketIdEqual(Long id) {
        return (root, query, criteriaBuilder) ->
                ofNullable(id)
                        .map(value -> criteriaBuilder.equal(root.get("id"), id))
                        .orElseGet(criteriaBuilder::conjunction);
    }

    public static Specification<Ticket> ticketFioEqual(String fio) {
        return (root, query, criteriaBuilder) ->
                ofNullable(fio)
                        .map(value -> criteriaBuilder.equal(root.get("fio"), fio))
                        .orElseGet(criteriaBuilder::conjunction);
    }

    public static Specification<Ticket> ticketTypeEqual(String type) {
        return (root, query, criteriaBuilder) ->
                ofNullable(type)
                        .filter(element -> Arrays.stream(TicketType.values()).anyMatch(el -> StringUtils.equals(el.name(), type)))
                        .map(value -> criteriaBuilder.equal(root.get("type"), TicketType.valueOf(type)))
                        .orElseGet(criteriaBuilder::conjunction);
    }
}
