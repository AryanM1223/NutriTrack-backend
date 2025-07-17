package com.example.NutriTrack.Services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.UserModel;

@Repository
public interface UserRepo extends JpaRepository<UserModel,Integer> {
    
}
