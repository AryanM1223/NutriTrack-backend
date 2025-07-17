package com.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "foodNutrient")
public class FoodNutrientModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String category;
    private String foodItem;
    private double caloriesPer100g;
    private double proteinPer100g;
    private double fiberPer100g;
    private double carbsPer100g;
    private double fatPer100g;
    private double sugarPer100g;

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

    public double getProteinPer100g() {
        return proteinPer100g;
    }

    public void setProteinPer100g(double proteinPer100g) {
        this.proteinPer100g = proteinPer100g;
    }

    public double getFiberPer100g() {
        return fiberPer100g;
    }

    public void setFiberPer100g(double fiberPer100g) {
        this.fiberPer100g = fiberPer100g;
    }

    public double getCarbsPer100g() {
        return carbsPer100g;
    }

    public void setCarbsPer100g(double carbsPer100g) {
        this.carbsPer100g = carbsPer100g;
    }

    public double getFatPer100g() {
        return fatPer100g;
    }

    public void setFatPer100g(double fatPer100g) {
        this.fatPer100g = fatPer100g;
    }

    public double getSugarPer100g() {
        return sugarPer100g;
    }

    public void setSugarPer100g(double sugarPer100g) {
        this.sugarPer100g = sugarPer100g;
    }

    public FoodNutrientModel() {}

    public FoodNutrientModel(String category, String foodItem, double caloriesPer100g, double proteinPer100g, double fiberPer100g, double carbsPer100g, double fatPer100g, double sugarPer100g) {
        this.category = category;
        this.foodItem = foodItem;
        this.caloriesPer100g = caloriesPer100g;
        this.proteinPer100g = proteinPer100g;
        this.fiberPer100g = fiberPer100g;
        this.carbsPer100g = carbsPer100g;
        this.fatPer100g = fatPer100g;
        this.sugarPer100g = sugarPer100g;
    }

}
