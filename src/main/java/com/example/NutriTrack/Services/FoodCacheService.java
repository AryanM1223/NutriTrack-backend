package com.example.NutriTrack.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class FoodCacheService {

    private static final String FOOD_CACHE_PREFIX = "food_nutrition:v2:"; 
    private static final long CACHE_DURATION_DAYS = 30; 

    @Autowired
    private StringRedisTemplate redisTemplate;

    private String generateCacheKey(String foodName, String quantityText) {
        return FOOD_CACHE_PREFIX + foodName.trim().toLowerCase() + ":" + quantityText.trim().toLowerCase();
    }

    public String getFoodData(String foodName, String quantityText) {
        return redisTemplate.opsForValue().get(generateCacheKey(foodName, quantityText));
    }

    public void setFoodData(String foodName, String quantityText, String nutritionalDataJson) {
        redisTemplate.opsForValue().set(generateCacheKey(foodName, quantityText), nutritionalDataJson, CACHE_DURATION_DAYS, TimeUnit.DAYS);
    }
}