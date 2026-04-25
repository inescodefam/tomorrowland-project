package com.yourname.tomorrowlandshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/products")
    public ResponseEntity<Void> listProducts() { return ResponseEntity.ok().build(); }

    @PostMapping("/products")
    public ResponseEntity<Void> createProduct() { return ResponseEntity.ok().build(); }

    @PutMapping("/products/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long id) { return ResponseEntity.ok().build(); }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) { return ResponseEntity.noContent().build(); }

    @PostMapping("/categories")
    public ResponseEntity<Void> createCategory() { return ResponseEntity.ok().build(); }

    @PutMapping("/categories/{id}")
    public ResponseEntity<Void> updateCategory(@PathVariable Long id) { return ResponseEntity.ok().build(); }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) { return ResponseEntity.noContent().build(); }

    @GetMapping("/orders")
    public String orders() { return "ok"; }

    @GetMapping("/audit-log")
    public String auditLog() { return "ok"; }
}
