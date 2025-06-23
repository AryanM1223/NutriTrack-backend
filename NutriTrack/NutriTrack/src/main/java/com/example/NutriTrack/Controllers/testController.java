package com.example.NutriTrack.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@RequestMapping("/ts")
public class testController {
    @GetMapping("/test")
public String hello() {
    return "It works!";
}
}
