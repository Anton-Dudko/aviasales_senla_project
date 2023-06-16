package eu.senla.aviasales.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class SendResponse {
    private String email;
    private String subject;
    private String message;
}