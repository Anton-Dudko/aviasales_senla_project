package eu.senla.userservice.request;

import eu.senla.common.entity.Role;
import eu.senla.userservice.entity.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFindRequest {
    private Language language;
    private Role role;
}
