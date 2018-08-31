package ru.kru.nick.mts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@SpringBootApplication
public class MtsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MtsApplication.class, args);
    }

    @Bean
    public ScheduledExecutorService dbExecutor() {
        return Executors.newScheduledThreadPool(5);
    }
}
