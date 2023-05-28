package eu.senla.userservice.service;

import eu.senla.common.entity.Role;
import eu.senla.userservice.entity.Language;
import eu.senla.userservice.entity.User;
import eu.senla.userservice.request.UserFindRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class UserSpecification implements Specification<User> {
    private UserFindRequest request;

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                    "%" + request.getEmail().toLowerCase() + "%"));
        }
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("username")),
                    "%" + request.getUsername().toLowerCase() + "%"));
        }
        if (request.getRole() != null) {
            predicates.add(criteriaBuilder.equal(root.get("role"), Role.valueOf(request.getRole())));
        }
        if (request.getLanguage() != null) {
            predicates.add(criteriaBuilder.equal(root.get("language"), Language.valueOf(request.getLanguage())));
        }
        if (request.getDateBirthFrom() != null && request.getDateBirthTo() == null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateBirth"), request.getDateBirthFrom()));
        }
        if (request.getDateBirthFrom() == null && request.getDateBirthTo() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateBirth"), request.getDateBirthTo()));
        }
        if (request.getDateBirthFrom() != null && request.getDateBirthTo() != null) {
            predicates.add(criteriaBuilder.between(root.get("dateBirth"), request.getDateBirthFrom(), request.getDateBirthTo().plusDays(1L)));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
