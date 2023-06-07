package eu.senla.aviasales.controller;

import eu.senla.aviasales.entity.EmailNotification;
import eu.senla.aviasales.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/notification")
public class AdminController {

    private final EmailNotificationService service;

    @GetMapping("/{id}")
    public EmailNotification findById(@PathVariable String id) {
        log.info("...Method findById");
        return service.findById(id);
    }

    @GetMapping
    public List<EmailNotification> findAll() {
        log.info("...Method findById");
        return service.findAll();
    }

    @DeleteMapping("/{id}")
    public EmailNotification deleteById(@PathVariable String id) {
        log.info("...Delete user:  id {}", id);
        return service.deleteById(id);
    }
}
