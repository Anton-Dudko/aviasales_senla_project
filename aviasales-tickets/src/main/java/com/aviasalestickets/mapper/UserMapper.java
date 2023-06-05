package com.aviasalestickets.mapper;

import com.aviasalestickets.model.dto.user.UserDetails;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class UserMapper {

    private final ObjectMapper objectMapper;

    public UserDetails getUserDetails(String userDetails) {
        return Optional.ofNullable(userDetails)
                .map(user -> {
                    try {
                        return objectMapper.readValue(userDetails, UserDetails.class);
                    } catch (JacksonException e) {
                        throw new RuntimeException(e.getOriginalMessage());
                    }
                }).orElseThrow(RuntimeException::new);
    }
}
