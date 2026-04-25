package com.yourname.tomorrowlandshop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    @GetMapping
    public String list() {
        return "ok";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id) {
        return "ok";
    }
}
