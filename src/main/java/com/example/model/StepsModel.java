package com.example.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "stepslog")
public class StepsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;
    private Integer stepCount;
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    private LocalDateTime createdAt;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public UserModel getUser() {
        return user;
    }
    public void setUser(UserModel user) {
        this.user = user;
    }
    public Integer getStepCount() {
        return stepCount;
    }
    public void setStepCount(Integer stepCount) {
        this.stepCount = stepCount;
    }
}
