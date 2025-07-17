package com.example.NutriTrack.Services;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.WorkoutModel;

public interface WorkoutRepo extends JpaRepository<WorkoutModel,Integer> {
    
}
