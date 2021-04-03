package com.github.brdr3.swsnetwork.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class SWSNConfigController {
    @GetMapping("/healthcheck")
    public String healthCheck() {
        return "Ok";
    }

    @GetMapping("/swagger")
    public RedirectView redirectSwagger(RedirectAttributes attributes) {
        return new RedirectView("/swagger-ui/");
    }
}
