package eu.senla.aviasales.controller;

import eu.senla.aviasales.exception.EmailSentNotFoundException;
import eu.senla.aviasales.model.dto.CustomEmailDto;
import eu.senla.aviasales.service.AdminEmailService;
import eu.senla.aviasales.service.EmailSentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@RestController
public class AdminController {
    private final EmailSentService emailSentService;
    private final AdminEmailService adminEmailService;

    @GetMapping("/email")
    public ResponseEntity<?> getEmailsByParams(@RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy")
                                               Date startDate,
                                               @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy")
                                               Date endDate,
                                               @RequestParam(value = "templateType", required = false) List<String> templateType,
                                               @RequestParam(value = "email", required = false) String email,
                                               @RequestParam(value = "page") int page,
                                               @RequestParam(value = "limit") int limit) {
        return ResponseEntity.ok(emailSentService.findAllByCriteria(startDate, endDate, templateType, email, page, limit));
    }

    @PostMapping("/email")
    public ResponseEntity<?> sendEmail(@RequestBody CustomEmailDto customEmailDto) {
        adminEmailService.sendCustomEmail(customEmailDto);
        return ResponseEntity.ok("Email sent");
    }

    @PostMapping("/email/{id}")
    public ResponseEntity<?> sendAgain(@PathVariable Long id) throws EmailSentNotFoundException {
        adminEmailService.sendAgain(id);
        return ResponseEntity.ok("Email resend successfully");
    }

}
