package com.example.NutriTrack.Controllers;

import com.example.Services.FoodRepo;
import com.example.Services.UserRepo;
import com.example.model.FoodModel;
import com.example.model.UserModel;
import com.example.NutriTrack.utils.OllamaClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/food")
public class FoodLogController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FoodRepo foodRepo;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping
    public ResponseEntity<?> addFood(@RequestBody FoodModel foodModel, @RequestParam int userId) {
        Optional<UserModel> existingUserOptional = userRepo.findById(userId);
        if (!existingUserOptional.isPresent()) {
            return ResponseEntity.badRequest().body("❌ User not found");
        }

        String foodName = foodModel.getFoodItem();
        String quantityText = foodModel.getQuantityText();

        String prompt = "You are a nutritionist. Estimate the total nutritional values for the following meal:\n"
        + "\"" + quantityText + " of " + foodName + "\".\n"
        + "Assume Indian home-cooked food. \n\n"
        + "CRITICAL: Respond with ONLY valid JSON. No comments, no explanations, no markdown formatting.\n"
        + "Use only numeric values (no text like 'approximately').\n\n"
        + "Required format:\n"
        + "{\n"
        + "  \"calories\": 305,\n"
        + "  \"protein\": 9,\n"
        + "  \"fiber\": 0\n"
        + "}";

        String modelResponse = OllamaClient.askModel(prompt);

     
       

       
        if (modelResponse == null || modelResponse.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Empty response from Ollama. Make sure Ollama is running on localhost:11434");
        }

        if (modelResponse.contains("\"error\"")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error from LLM service: " + modelResponse);
        }

        try {
            String cleanedResponse = extractJsonFromResponse(modelResponse);
            System.out.println("=== DEBUG: Cleaned JSON ===\n" + cleanedResponse + "\n=== End Cleaned JSON ===");

            JsonNode jsonNode = objectMapper.readTree(cleanedResponse);

            double calories = jsonNode.has("calories") ? jsonNode.get("calories").asDouble(0) : 0;
            double protein = jsonNode.has("protein") ? jsonNode.get("protein").asDouble(0) : 0;
            double fiber = jsonNode.has("fiber") ? jsonNode.get("fiber").asDouble(0) : 0;

            System.out.println("Calories: " + calories);
            System.out.println("Protein: " + protein);
            System.out.println("Fiber: " + fiber);
          

            foodModel.setCalories(calories);
            foodModel.setTotalProtein(protein);
            foodModel.setTotalFiber(fiber);
        } catch (Exception e) {
           
            System.err.println("Raw response: " + modelResponse);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to parse LLM response: " + e.getMessage() + ". Raw response: " + modelResponse);
        }

        foodModel.setUser(existingUserOptional.get());
        FoodModel savedFood = foodRepo.save(foodModel);
        return ResponseEntity.ok(savedFood);
    }

    private String extractJsonFromResponse(String modelResponse) {
       
        modelResponse = modelResponse.trim();
        
      
        int start = modelResponse.indexOf('{');
        int end = modelResponse.lastIndexOf('}');

        if (start != -1 && end != -1 && end > start) {
            String jsonString = modelResponse.substring(start, end + 1);
            
           
            jsonString = jsonString.replaceAll("\\r|\\n|\\t", " ").replaceAll("\\s+", " ").trim();
            
            return jsonString;
        } else {
         
            if (modelResponse.startsWith("{") && modelResponse.endsWith("}")) {
                return modelResponse;
            }
            throw new IllegalArgumentException("No valid JSON object found in model response: " + modelResponse);
        }
    }

    @GetMapping("/getfood/{id}")
    public ResponseEntity<ArrayList<FoodModel>> getFood(@PathVariable int id) {
        Optional<UserModel> exisOptional = userRepo.findById(id);
        if (!exisOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        ArrayList<FoodModel> foodlogs = foodRepo.findByUser(exisOptional.get());
        return ResponseEntity.ok(foodlogs);
    }

    @GetMapping("/summary")
public ResponseEntity<?> getDailySummary(@RequestParam int userId, @RequestParam String date) {
    Optional<UserModel> userOptional = userRepo.findById(userId);
    if (!userOptional.isPresent()) {
        return ResponseEntity.badRequest().body("❌ User not found");
    }

    LocalDate localDate = LocalDate.parse(date);
    List<FoodModel> foodList = foodRepo.findByUserAndDate(userOptional.get(), localDate);

    double totalCalories = 0, totalProtein = 0, totalFiber = 0;
    for (FoodModel food : foodList) {
        totalCalories += food.getCalories();
        totalProtein += food.getProtein();
        totalFiber += food.getFiber();
    }

    Map<String, Object> response = new HashMap<>();
    response.put("date", localDate);
    response.put("totalCalories", totalCalories);
    response.put("totalProtein", totalProtein);
    response.put("totalFiber", totalFiber);
    response.put("totalItems", foodList.size());

    return ResponseEntity.ok(response);
}

}