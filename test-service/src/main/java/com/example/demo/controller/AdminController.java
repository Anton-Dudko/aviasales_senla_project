package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/confident")
public class AdminController {

    @GetMapping("/info")
    public String getConfidentInfo() {
        log.info("Trying to send Confidential info for ADMIN");
        return "Confidential info for ADMIN";
    }

}
