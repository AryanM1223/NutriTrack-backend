package com.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "foodNutirent")
public class FoodNutrientModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String category;
    private String foodItem;
    private double caloriesPer100g;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(String foodItem) {
        this.foodItem = foodItem;
    }

    public double getCaloriesPer100g() {
        return caloriesPer100g;
    }

    public void setCaloriesPer100g(double caloriesPer100g) {
        this.caloriesPer100g = caloriesPer100g;
    }

    public FoodNutrientModel() {}

    public FoodNutrientModel(String category, String foodItem, double caloriesPer100g) {
        this.category = category;
        this.foodItem = foodItem;
        this.caloriesPer100g = caloriesPer100g;
    }

}
