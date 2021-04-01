package com.github.brdr3.swsnetwork.controller;

import com.github.brdr3.swsnetwork.dto.RebelDTO;
import com.github.brdr3.swsnetwork.service.RebelsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/rebel")
public class RebelsController {

    private final RebelsService rebelsService;

    @Autowired
    public RebelsController(RebelsService rs) {
        this.rebelsService = rs;
    }

    @PostMapping("/")
    public ResponseEntity<?> insertRebel(@RequestBody RebelDTO rebel) {
        try {
            RebelDTO rebelDTO = this.rebelsService.insertRebel(rebel);
            return new ResponseEntity<>(rebelDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }

    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getRebel(@PathVariable UUID id) {
        RebelDTO rebelDTO = this.rebelsService.getRebel(id);
        if(rebelDTO != null) {
            return new ResponseEntity<>(rebelDTO, HttpStatus.ACCEPTED);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not find any rebel with id '" + id.toString() + "'");
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> getRebel(@PathVariable String name) {
        RebelDTO rebelDTO = this.rebelsService.getRebelByName(name);
        if(rebelDTO != null) {
            return new ResponseEntity<>(rebelDTO, HttpStatus.ACCEPTED);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not find any rebel with name '" + name + "'");
    }
}