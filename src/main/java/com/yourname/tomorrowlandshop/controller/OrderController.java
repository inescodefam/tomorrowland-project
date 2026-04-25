package com.yourname.tomorrowlandshop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @PostMapping("/checkout")
    public String checkout() {
        return "ok";
    }

    @GetMapping("/history")
    public String history() {
        return "ok";
    }
}
