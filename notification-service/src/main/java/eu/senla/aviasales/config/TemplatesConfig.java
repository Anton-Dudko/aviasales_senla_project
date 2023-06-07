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
        return topics.values()
                .stream()
                .map(Topic::getTopicName)
                .toList();
    }

    public Topic getTopicByName(String topicName) throws TopicNotFoundException {
        return topics.values().stream()
                .filter(topic -> topic.getTopicName().equals(topicName))
                .findFirst().orElseThrow(() -> new TopicNotFoundException("Cannot find topic: " + topicName + "."));
    }
    public static class Topic {
        private String topicName;
        private String template;
        private String localization;
        private String subject;

        public String getTopicName() {
            return topicName;
        }

        public void setTopicName(final String topicName) {
            this.topicName = topicName;
        }

        public String getTemplate() {
            return template;
        }

        public void setTemplate(final String template) {
            this.template = template;
        }

        public String getLocalization() {
            return localization;
        }

        public void setLocalization(final String localization) {
            this.localization = localization;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(final String subject) {
            this.subject = subject;
        }
    }
}
