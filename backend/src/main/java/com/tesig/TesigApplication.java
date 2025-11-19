package com.tesig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TesigApplication {

    public static void main(String[] args) {
        SpringApplication.run(TesigApplication.class, args);
    }
}
