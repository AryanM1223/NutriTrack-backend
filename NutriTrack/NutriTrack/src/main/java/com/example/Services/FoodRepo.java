package com.example.Services;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.FoodModel;
import com.example.model.UserModel;

public interface FoodRepo extends JpaRepository<FoodModel,Integer> {
    ArrayList<FoodModel> findByUser(UserModel user);
    @Query("SELECT f FROM FoodModel f WHERE f.user = :user AND DATE(f.createdAt) = :date")
    List<FoodModel> findByUserAndDate(@Param("user") UserModel user, @Param("date") LocalDate date);
}
