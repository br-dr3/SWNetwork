package com.github.brdr3.swsnetwork.controller;

import com.github.brdr3.swsnetwork.dto.NegotiateItemsDTO;
import com.github.brdr3.swsnetwork.dto.RebelBaseDTO;
import com.github.brdr3.swsnetwork.dto.RebelDTO;
import com.github.brdr3.swsnetwork.dto.ReportBetrayalDTO;
import com.github.brdr3.swsnetwork.service.RebelsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/rebel")
@RequiredArgsConstructor
public class RebelsController {

    private final RebelsService rebelsService;

    @PostMapping("/")
    public ResponseEntity<?> insertRebel(@RequestBody RebelDTO rebel) throws Exception {
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
        if (rebelDTO != null) {
            return new ResponseEntity<>(rebelDTO, HttpStatus.OK);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Could not find any rebel with id '" + id.toString() + "'");
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> getRebel(@PathVariable String name) {
        RebelDTO rebelDTO = this.rebelsService.getRebelByName(name);
        if (rebelDTO != null) {
            return new ResponseEntity<>(rebelDTO, HttpStatus.OK);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not find any rebel with name '" + name + "'");
    }

    @PutMapping("/id/{id}/rebelBase/update")
    public ResponseEntity<?> updateRebelBase(@PathVariable UUID id, @RequestBody RebelBaseDTO rebelBase) {
        try {
            RebelDTO rebelDTO = this.rebelsService.updateRebelBase(id, rebelBase);
            return new ResponseEntity<>(rebelDTO, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }

    @PostMapping("/reportBetrayal")
    public ResponseEntity<?> reportBetrayal(@RequestBody ReportBetrayalDTO reportBetrayal) {
        try {
            ReportBetrayalDTO reportBetrayalDTO = this.rebelsService.reportBetrayal(reportBetrayal);
            return new ResponseEntity<>(reportBetrayalDTO, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }

    @PostMapping("/negotiateItems")
    public ResponseEntity<?> negotiateItems(@RequestBody NegotiateItemsDTO negotiation) throws Exception {
        try {
            this.rebelsService.negotiateItems(negotiation);
            return new ResponseEntity<>("Ok", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }
}