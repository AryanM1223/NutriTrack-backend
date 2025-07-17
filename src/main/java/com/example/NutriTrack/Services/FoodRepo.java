package com.example.NutriTrack.Services;

import com.example.model.FoodModel;
import com.example.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface FoodRepo extends JpaRepository<FoodModel, Integer> {

    ArrayList<FoodModel> findByUser(UserModel user);

    @Query("SELECT f FROM FoodModel f WHERE f.user = :user AND CAST(f.createdAt AS date) = :date")
    List<FoodModel> findByUserAndDate(@Param("user") UserModel user, @Param("date") LocalDate date);
}