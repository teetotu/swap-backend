package ru.swap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SwapApplication {
    public static void main(String[] args) {
        SpringApplication.run(SwapApplication.class, args);
    }
}
