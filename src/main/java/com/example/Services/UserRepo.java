package com.example.Services;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.UserModel;

public interface UserRepo extends JpaRepository<UserModel,Integer> {
    
}
