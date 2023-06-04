package eu.senla.aviasales.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Document("emailnotification")
public class EmailNotification {
    @Id
    private Long id;
    private String templateType;
    private String subject;
    private String receiver;
    private Map<String, Object> templateVariables = new HashMap<>();
    private LocalDate dateFirstSend;
}
