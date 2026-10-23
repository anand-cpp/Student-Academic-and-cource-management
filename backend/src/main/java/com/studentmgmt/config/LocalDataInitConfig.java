package com.studentmgmt.config;

import com.studentmgmt.service.DemoDataSeederService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
@RequiredArgsConstructor
@Slf4j
public class LocalDataInitConfig implements CommandLineRunner {

    private final DemoDataSeederService demoDataSeederService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Local environment detected. Initializing clean, internally consistent demo data...");
        demoDataSeederService.resetAndSeedDemoData();
        log.info("Local environment ready for authenticated multi-role exploration.");
    }
}
