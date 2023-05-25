package eu.senla.aviasales.service.validator;

import static eu.senla.aviasales.model.constant.message.IncorrectDataExceptionMessage.EMAIL_CANNOT_BE_EMPTY;
import static eu.senla.aviasales.model.constant.message.IncorrectDataExceptionMessage.NAME_CANNOT_BE_NULL;
import static eu.senla.aviasales.model.constant.message.IncorrectDataExceptionMessage.USER_DATA_CANNOT_BE_NULL;
import eu.senla.aviasales.exception.IncorrectDataException;
import eu.senla.aviasales.model.dto.UserDataDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

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
        } else if (userDataDto.getEmail() == null || Strings.isEmpty(userDataDto.getEmail())) {
            log.warn(EMAIL_CANNOT_BE_EMPTY);
            throw new IncorrectDataException(EMAIL_CANNOT_BE_EMPTY);
        } else if (userDataDto.getUserName() == null || Strings.isEmpty(userDataDto.getUserName())) {
            log.warn(NAME_CANNOT_BE_NULL);
            throw new IncorrectDataException(NAME_CANNOT_BE_NULL);
        }
    }
}
