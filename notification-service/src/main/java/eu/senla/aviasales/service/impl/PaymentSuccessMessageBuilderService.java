package eu.senla.aviasales.service.impl;

import static eu.senla.aviasales.model.constant.Subject.PAYMENT_SUCCESS;
import eu.senla.aviasales.model.dto.PaymentSuccessEventDto;
import eu.senla.aviasales.service.EmailService;
import eu.senla.aviasales.service.MessageBuilderService;
import eu.senla.aviasales.service.util.UserDtoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@RequiredArgsConstructor
@Service
public class PaymentSuccessMessageBuilderService implements MessageBuilderService<PaymentSuccessEventDto> {
    private final SpringTemplateEngine templateEngine;
    private final EmailService emailService;
    private final UserDtoUtil userDtoUtil;

    @Override
    public void buildAndSend(final PaymentSuccessEventDto dto) throws MessagingException {
        userDtoUtil.setMissedLanguageField(dto);
        Context context = new Context();
        context.setVariable("name", dto.getUserName());
        context.setVariable("paymentInfo", dto.getPaymentInfo());
        context.setVariable("amountPayable", dto.getAmountPayable());
        context.setVariable("paymentDate", dto.getPaymentDate());
        String html = templateEngine.process("payment_success_" + dto.getUserLanguage(), context);
        emailService.sendEmail(dto.getEmail(), PAYMENT_SUCCESS, html);
    }
}
