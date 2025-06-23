package com.example.model;


import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "waterlog")
public class WaterModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;
    private Double amountInml;
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
    public Double getAmountInml() {
        return amountInml;
    }
    public void setAmountInml(Double amountInml) {
        this.amountInml = amountInml;
    }
  

}
