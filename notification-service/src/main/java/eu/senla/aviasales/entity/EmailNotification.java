package eu.senla.aviasales.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

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
    @MongoId
    private ObjectId id;
    @Field
    private String templateType;
    @Field
    private String subject;
    @Field
    private String receiver;
    @Field
    private Map<String, Object> templateVariables = new HashMap<>();
    @Field
    private LocalDate dateFirstSend;
    @Field
    private int countTrySend;
}
