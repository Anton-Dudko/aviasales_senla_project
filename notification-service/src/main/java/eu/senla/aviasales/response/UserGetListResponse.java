package eu.senla.aviasales.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserGetListResponse {
    private Integer count;
    private List<UserResponse> userResponseList;
}
