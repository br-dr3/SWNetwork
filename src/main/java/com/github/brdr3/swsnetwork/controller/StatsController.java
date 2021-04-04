package com.github.brdr3.swsnetwork.controller;

import com.github.brdr3.swsnetwork.service.RebelsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stats")
public class StatsController {
    private final RebelsService rebelsService;

    @Autowired
    public StatsController(RebelsService rs) {
        this.rebelsService = rs;
    }

    @GetMapping("/")
    public ResponseEntity<?> getStats() {
        return new ResponseEntity<>(rebelsService.getStats(), HttpStatus.OK);
    }
}
