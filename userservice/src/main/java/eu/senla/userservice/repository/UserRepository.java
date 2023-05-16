package eu.senla.userservice.repository;

import eu.senla.userservice.entity.Role;
import eu.senla.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Page<User> findAllByRole(Pageable pageable, Role role);

    Optional<User> findByEmail(String email);

}
