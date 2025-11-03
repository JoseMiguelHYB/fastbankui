package com.josemiguelhyb.fastbank.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomeController {

    // Mantenemos el endpoint /api/hello para no romper el frontend existente
    @GetMapping("/hello")
    public String home() {
        return "✅ FastBank BACKEND operativo — la API responde correctamente en /api/hello. Todo listo.";
    }
}
