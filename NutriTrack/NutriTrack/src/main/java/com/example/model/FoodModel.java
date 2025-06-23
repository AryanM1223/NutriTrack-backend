package com.example.model;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import jakarta.persistence.*;


@Entity
@Table(name = "foodlog")
public class FoodModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String foodItem;
    private String quantityText; 
    public String getQuantityText() {
        return quantityText;
    }
    public void setQuantityText(String quantityText) {
        this.quantityText = quantityText;
    }
    private double calories;
    @Column
    private double totalProtein;

    @Column
    private double totalFiber;

    
    public double getTotalProtein() {
        return totalProtein;
    }
    public void setTotalProtein(double totalProtein) {
        this.totalProtein = totalProtein;
    }
    public double getTotalFiber() {
        return totalFiber;
    }
    public void setTotalFiber(double totalFiber) {
        this.totalFiber = totalFiber;
    }
    @Enumerated(EnumType.STRING)
    private MealType mealType;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    @CreationTimestamp
    @Column(updatable = false)
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    private LocalDateTime createdAt;


  
    public UserModel getUser() {
        return user;
    }
    public void setUser(UserModel user) {
        this.user = user;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getFoodItem() {
        return foodItem;
    }
    public void setFoodItem(String food_item) {
        this.foodItem = food_item;
    }
    public double getCalories() {
        return calories;
    }
    public void setCalories(double calories) {
        this.calories = calories;
    }
    public MealType getMealType() {
        return mealType;
    }
    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }
   
    
}

