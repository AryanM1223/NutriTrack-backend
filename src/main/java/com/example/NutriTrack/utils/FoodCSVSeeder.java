package com.example.NutriTrack.utils;

import com.example.Services.FoodNutrientRepo;
import com.example.model.FoodNutrientModel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

//@Component
public class FoodCSVSeeder {

    @Autowired
    private FoodNutrientRepo foodNutrientRepo;

    @PostConstruct
    public void seedFoodData() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("food_calories.csv");
            if (is == null) {
                System.err.println("❌ food_calories.csv not found in resources");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            boolean headerSkipped = false;

            while ((line = reader.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true; // skip header
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length < 4) continue;

                String category = parts[0].trim();
                String foodItem = parts[1].trim();
                String caloriesStr = parts[3].trim().toLowerCase().replace(" cal", "");

                double calories;
                try {
                    calories = Double.parseDouble(caloriesStr);
                } catch (NumberFormatException e) {
                    System.err.println("⚠️ Could not parse calories for: " + foodItem);
                    continue;
                }

                if (!foodNutrientRepo.existsByFoodItemIgnoreCase(foodItem)) {
                    FoodNutrientModel item = new FoodNutrientModel(category, foodItem, calories);
                    foodNutrientRepo.save(item);
                    System.out.println("✅ Inserted: " + foodItem + " (" + calories + " cal)");
                }
            }

            System.out.println("🎉 Seeding complete.");
        } catch (Exception e) {
            System.err.println("💥 Error reading food_calories.csv");
            e.printStackTrace();
        }
    }
}
