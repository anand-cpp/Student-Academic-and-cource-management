package com.studentmgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(basePackages = "com.studentmgmt")
@EntityScan(basePackages = "com.studentmgmt.model")
@EnableJpaRepositories(basePackages = "com.studentmgmt.repository")
@EnableTransactionManagement
@EnableAsync
public class StudentAcademicManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentAcademicManagementApplication.class, args);
    }
}