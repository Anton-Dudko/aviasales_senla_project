package eu.senla.aviasales.response;

import eu.senla.aviasales.entity.EmailNotification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListEmailNotification {
    private Long count;
    private List<EmailNotification> emailNotificationList;
}
