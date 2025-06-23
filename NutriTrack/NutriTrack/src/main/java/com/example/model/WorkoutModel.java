package com.example.model;


import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "workoutlog")
public class WorkoutModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;
    @Enumerated(EnumType.STRING)
    private WorkoutType workoutType;
    @Enumerated(EnumType.STRING)
    private Intensity intensity;
    private double durationInMins;
    private Double calBurn;
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
    public WorkoutType getWorkoutType() {
        return workoutType;
    }
    public void setWorkoutType(WorkoutType workoutType) {
        this.workoutType = workoutType;
    }
    public Intensity getIntensity() {
        return intensity;
    }
    public void setIntensity(Intensity intensity) {
        this.intensity = intensity;
    }
    public double getDurationInMins() {
        return durationInMins;
    }
    public void setDurationInMins(double durationInMins) {
        this.durationInMins = durationInMins;
    }
    public Double getCalBurn() {
        return calBurn;
    }
    public void setCalBurn(Double calBurn) {
        this.calBurn = calBurn;
    }
 
}
