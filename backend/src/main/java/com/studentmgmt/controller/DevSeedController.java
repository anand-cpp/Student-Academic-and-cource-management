package com.studentmgmt.controller;

import com.studentmgmt.service.DemoDataSeederService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dev")
@Profile({"local", "dev", "test"})
@RequiredArgsConstructor
@Slf4j
public class DevSeedController {

    private final DemoDataSeederService demoDataSeederService;

    @PostMapping("/reset-and-seed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> resetAndSeed() {
        log.info("Received request to safely reset and reseed demo dataset");
        Map<String, Object> result = demoDataSeederService.resetAndSeedDemoData();
        return ResponseEntity.ok(result);
    }
}
