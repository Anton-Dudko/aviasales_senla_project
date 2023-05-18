package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/finance")
public class Controller {

    @GetMapping("/info")
    public String getInfo(@RequestHeader("x-auth-user") String authUser) {
        log.info("User ----> {}", authUser);
        return "All is ok";
    }
}
