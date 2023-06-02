package eu.senla.aviasales.model.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import java.util.Date;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Getter
@Setter
@Entity
@Table(name = "email_sent")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class EmailSent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String templateType;
    private String subject;
    private String receiver;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> templateVariables;
    private Boolean isSent;
    private String errorMessage;
    private Date sentDate;

    public EmailSent() {

    }

    public EmailSent(String receiver, String subject, String templateType, Map<String, Object> templateVariables, boolean isSent) {
        this.receiver = receiver;
        this.subject = subject;
        this.templateType = templateType;
        this.templateVariables = templateVariables;
        this.isSent = isSent;
        this.sentDate = new Date();
    }

    public EmailSent(String receiver, String subject, String templateType, Map<String, Object> templateVariables,
                     String errorMessage, boolean isSent) {
        this.receiver = receiver;
        this.subject = subject;
        this.templateType = templateType;
        this.templateVariables = templateVariables;
        this.errorMessage = errorMessage;
        this.isSent = isSent;
        this.sentDate = new Date();
    }

}
