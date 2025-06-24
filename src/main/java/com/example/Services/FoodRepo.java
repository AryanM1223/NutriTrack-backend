package com.example.Services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.FoodModel;
import com.example.model.UserModel;

public interface FoodRepo extends JpaRepository<FoodModel,Integer> {
    ArrayList<FoodModel> findByUser(UserModel user);
    @Query("SELECT f FROM FoodModel f WHERE f.user = :user AND DATE(f.createdAt) = :date")
    List<FoodModel> findByUserAndDate(@Param("user") UserModel user, @Param("date") LocalDate date);
}
