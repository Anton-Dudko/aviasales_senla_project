package eu.senla.userservice.security;

import eu.senla.userservice.entity.User;
import eu.senla.userservice.exception.ExceptionMessageConstant;
import eu.senla.userservice.exception.custom.NotFoundException;
import eu.senla.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstant.NOT_FOUND_USER));
        return UserDetailsImpl.fromUserToUserDetails(user);
    }
}
