package eu.senla.userservice.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TextResponse {
    private String username;
    private String email;
    private String message;
}