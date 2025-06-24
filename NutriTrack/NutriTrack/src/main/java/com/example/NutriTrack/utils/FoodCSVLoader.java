package com.example.NutriTrack.utils;

import com.example.Services.FoodNutrientRepo;
import com.example.model.FoodNutrientModel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//@Component
public class FoodCSVLoader {

    @Autowired
    private FoodNutrientRepo foodNutrientRepo;

    @PostConstruct
    public void loadData() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("food_calories.csv")))) {

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) { 
                    firstLine = false;
                    continue;
                }

                String[] fields = line.split(",", -1); 
                if (fields.length < 4) continue;

                String category = fields[0].trim();
                String foodItem = fields[1].trim();
                String caloriesRaw = fields[3].trim(); 

                double calories = Double.parseDouble(caloriesRaw.replace("cal", "").trim());

                FoodNutrientModel model = new FoodNutrientModel(category, foodItem, calories);
                foodNutrientRepo.save(model);
            }

            System.out.println("Finished loading food nutrient data.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load food nutrient data");
        }
    }
}
