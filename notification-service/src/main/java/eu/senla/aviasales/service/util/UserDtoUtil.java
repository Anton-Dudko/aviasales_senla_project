package eu.senla.aviasales.service.util;

import eu.senla.aviasales.config.TemplatesLanguagesConfig;
import eu.senla.aviasales.model.dto.UserDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@RequiredArgsConstructor
@Component
public class UserDtoUtil {
    private final TemplatesLanguagesConfig config;

    public void setMissedLanguageField(UserDataDto userDataDto) {
        if (userDataDto.getUserLanguage() == null
                || userDataDto.getUserLanguage().isBlank()
                || !config.getLanguages().contains(userDataDto.getUserLanguage().toLowerCase())) {
            userDataDto.setUserLanguage("en");
        }
    }
}
