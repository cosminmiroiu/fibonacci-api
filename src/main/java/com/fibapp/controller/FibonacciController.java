package com.fibapp.controller;

import com.fibapp.service.FibonacciService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fibonacci")
public class FibonacciController {

    private final FibonacciService service;

    public FibonacciController(FibonacciService service) {
        this.service = service;
    }

    @PostMapping("/next/{clientId}")
    public ResponseEntity<Long> next(@PathVariable String clientId) {
        return ResponseEntity.ok(service.next(clientId));
    }

    @PostMapping("/back/{clientId}")
    public ResponseEntity<String> back(@PathVariable String clientId) {
        return ResponseEntity.ok(service.back(clientId));
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<List<Long>> list(@PathVariable String clientId) {
        return ResponseEntity.ok(service.list(clientId));
    }

}
