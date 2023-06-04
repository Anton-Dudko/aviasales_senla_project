package eu.senla.aviasales.service.validator;

import eu.senla.aviasales.exception.custom.IncorrectDataException;
import eu.senla.aviasales.model.dto.UserDataDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import static eu.senla.aviasales.exception.ExceptionMessageConstants.EMAIL_CANNOT_BE_EMPTY;
import static eu.senla.aviasales.exception.ExceptionMessageConstants.NAME_CANNOT_BE_NULL;
import static eu.senla.aviasales.exception.ExceptionMessageConstants.USER_DATA_CANNOT_BE_NULL;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Slf4j
@Service
public class UserDataValidator implements ValidatorService<UserDataDto> {

    @Override
    public void validate(final UserDataDto userDataDto) throws Exception {
        if (userDataDto == null) {
            log.warn(USER_DATA_CANNOT_BE_NULL);
            throw new IncorrectDataException(USER_DATA_CANNOT_BE_NULL);
        } else if (StringUtils.isEmpty(userDataDto.getEmail())) {
            log.warn(EMAIL_CANNOT_BE_EMPTY);
            throw new IncorrectDataException(EMAIL_CANNOT_BE_EMPTY);
        } else if (StringUtils.isEmpty(userDataDto.getUserName())) {
            log.warn(NAME_CANNOT_BE_NULL);
            throw new IncorrectDataException(NAME_CANNOT_BE_NULL);
        }
    }
}
