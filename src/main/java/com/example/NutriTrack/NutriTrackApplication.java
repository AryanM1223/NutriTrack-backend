package com.example.NutriTrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.example.NutriTrack.Services")
@EntityScan("com.example.model")
public class NutriTrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(NutriTrackApplication.class, args);
    }
    
}
