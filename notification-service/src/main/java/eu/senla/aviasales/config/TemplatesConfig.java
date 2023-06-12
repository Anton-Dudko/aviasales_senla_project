package eu.senla.aviasales.config;

import eu.senla.aviasales.exception.custom.TopicNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.email")
public class TemplatesConfig {

    private List<String> languages;
    private String localizationFolder;
    private Map<String, Topic> topics;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    public List<String> getTopicNames() {
        return topics.values().stream()
                .map(Topic::getTopicName)
                .toList();
    }

    public Topic getTopicByName(String topicName) {
        return topics.values().stream()
                .filter(topic -> topic.getTopicName().equals(topicName))
                .findFirst().orElseThrow(() -> new TopicNotFoundException("Cannot find topic: " + topicName + "."));
    }

    @Getter
    @Setter
    public static class Topic {
        private String topicName;
        private String template;
        private String localization;
        private String subject;
    }
}
