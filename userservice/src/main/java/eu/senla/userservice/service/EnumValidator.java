package eu.senla.userservice.service;

import eu.senla.common.enam.Language;
import eu.senla.common.enam.Role;
import eu.senla.userservice.exception.ExceptionMessageConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Component;

@Component
public class EnumValidator {

    public Role checkRoleEnum(String value) {
        if (StringUtils.isEmpty(value)) {
            return Role.ROLE_USER;
        } else if (EnumUtils.isValidEnumIgnoreCase(Role.class, value)) {
            return Role.valueOf(value.toUpperCase());
        } else {
            throw new IllegalArgumentException(ExceptionMessageConstants.NOT_VALID_ROLE);
        }
    }

    public Language checkLanguageEnum(String value) {
        if (StringUtils.isEmpty(value)) {
            return Language.EN;
        } else if (EnumUtils.isValidEnumIgnoreCase(Language.class, value)) {
            return Language.valueOf(value.toUpperCase());
        } else {
            throw new IllegalArgumentException(ExceptionMessageConstants.NOT_VALID_LANGUAGE);
        }
    }
}
