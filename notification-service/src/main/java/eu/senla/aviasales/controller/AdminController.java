package eu.senla.aviasales.controller;

import eu.senla.aviasales.model.dto.CustomEmailDto;
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

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@RestController
public class AdminController {

    @GetMapping("/email")
    public ResponseEntity<?> getEmailsByParams(@RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy")
                                               Date startDate,
                                               @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy")
                                               Date endDate,
                                               @RequestParam(value = "templateType", required = false) String templateType,
                                               @RequestParam(value = "email", required = false) String email) {
        return ResponseEntity.ok("");
    }

    @PostMapping("/email")
    public ResponseEntity<?> sendEmail(@RequestBody CustomEmailDto customEmailDto) {
        return ResponseEntity.ok("");
    }

    @PostMapping("/email/{id}") //TODO post - ?
    public ResponseEntity<?> sendAgain(@PathVariable Long id) {
        return ResponseEntity.ok("");
    }

}
