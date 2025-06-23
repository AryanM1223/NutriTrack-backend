package com.example.Services;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.FoodNutrientModel;

public interface FoodNutrientRepo extends JpaRepository<FoodNutrientModel,Integer> {
    List<FoodNutrientModel> findByFoodItemIgnoreCase(String foodItem);

    boolean existsByFoodItemIgnoreCase(String foodItem);

}

