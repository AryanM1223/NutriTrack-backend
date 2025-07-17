package com.example.NutriTrack.Services;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.StepsModel;

public interface StepsRepo extends JpaRepository<StepsModel,Integer>{
    
}
