package eu.senla.aviasales.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.email")
public class TemplatesLanguagesConfig {
    private List<String> languages;

}
