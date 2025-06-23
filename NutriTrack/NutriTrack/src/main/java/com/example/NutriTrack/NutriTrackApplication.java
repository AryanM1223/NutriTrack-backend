package com.example.NutriTrack;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.example.NutriTrack.utils.OllamaClient;

@SpringBootApplication
@EnableJpaRepositories("com.example.Services")
@EntityScan("com.example.model")
public class NutriTrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(NutriTrackApplication.class, args);
    }

    @Bean
    public CommandLineRunner testOllama() {
        return args -> {
            String response = OllamaClient.askModel("What is 2+2? Respond only with a number.");
            System.out.println("Ollama response: " + response);
        };
    }
}
