package eu.senla.aviasales.service.util;

import eu.senla.aviasales.model.dto.UserDataDto;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
public class UserDtoUtil {
    public static void setMissedLanguageField(UserDataDto userDataDto) {
        if (userDataDto.getUserLanguage() == null
                || userDataDto.getUserLanguage().isBlank()) {
            userDataDto.setUserLanguage("en");
        }
    }
}
