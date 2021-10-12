package com.example.videojuegos.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Controlador {
    @GetMapping(value = "/")
    public String index(Model model){
        String saludo = "Hola Thymeleaf";
        model.addAttribute("saludo", saludo);
        return "index"; // el directorio principal que toma spring es templates
    }

    @GetMapping(value = "/saludo2")
    public String saludo2(Model model){
        String saludo = "Hola Como estas";
        model.addAttribute("saludo", saludo);
        return "index"; // el directorio principal que toma spring es templates
    }
}
