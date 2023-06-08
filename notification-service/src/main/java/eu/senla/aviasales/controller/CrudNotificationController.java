package eu.senla.aviasales.controller;

import eu.senla.aviasales.entity.EmailNotification;
import eu.senla.aviasales.response.ListEmailNotification;
import eu.senla.aviasales.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/notification")
public class CrudNotificationController {

    private final EmailNotificationService service;

    @GetMapping("/{id}")
    public EmailNotification findById(@PathVariable String id) {
        log.info("...Method findById");
        return service.findById(id);
    }

    @GetMapping
    public ListEmailNotification findAll() {
        log.info("...Method findById");
        return service.findAll();
    }

    @DeleteMapping("/{id}")
    public EmailNotification deleteById(@PathVariable String id) {
        log.info("...Delete user:  id {}", id);
        return service.deleteById(id);
    }
}
