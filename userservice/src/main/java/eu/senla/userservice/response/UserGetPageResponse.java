package eu.senla.userservice.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class UserGetPageResponse {
    private Integer count;
    private List<UserResponse> userResponseList;
}
