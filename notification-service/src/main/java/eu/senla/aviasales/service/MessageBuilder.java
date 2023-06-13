package eu.senla.aviasales.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.senla.aviasales.config.TemplatesConfig;
import eu.senla.aviasales.entity.EmailNotification;
import eu.senla.aviasales.exception.custom.TopicNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class MessageBuilder {
    private final TemplatesConfig templatesConfig;
    private final SpringTemplateEngine templateEngine;
    private final ObjectMapper objectMapper;

    public Map<String, String> build(@NotNull EmailNotification emailNotification) {
        log.info("... method build");
        try {
            setMissedLanguageField(emailNotification.getTemplateVariables());

            Map<String, String> result = new HashMap<>();
            TemplatesConfig.Topic topic = templatesConfig.getTopicByName(emailNotification.getTemplateType());
            if (StringUtils.isNotEmpty(topic.getSubject())) {
                result.put("subject", topic.getSubject());
            } else {
                result.put("subject", emailNotification.getSubject());
            }

            Context context = new Context();
            context.setVariables(emailNotification.getTemplateVariables());
            context.setVariables(getLocalizationVariables(topic, (String) emailNotification.getTemplateVariables().get("userLanguage")));
            result.put("html", templateEngine.process(topic.getTemplate(), context));
            return result;
        } catch (TopicNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setMissedLanguageField(Map<String, Object> dtoVariables) {
        String userLang = (String) dtoVariables.get("userLanguage");
        if (StringUtils.isEmpty(userLang) || !templatesConfig.getLanguages().contains(userLang.toLowerCase())) {
            dtoVariables.put("userLanguage", "en");
        }
    }

    private Map<String, Object> getLocalizationVariables(TemplatesConfig.Topic topic, String language)
            throws IOException {
        return objectMapper.readValue(new ClassPathResource(
                templatesConfig.getLocalizationFolder()
                        + topic.getLocalization()
                        + "_"
                        + language.toLowerCase()
                        + ".json"
        ).getInputStream(), new TypeReference<>() {
        });
    }
}
