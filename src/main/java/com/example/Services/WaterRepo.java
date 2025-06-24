package com.example.Services;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.WaterModel;

public interface WaterRepo extends JpaRepository<WaterModel,Integer>{
    
}
