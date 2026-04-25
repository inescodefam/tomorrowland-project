package com.yourname.tomorrowlandshop.controller;

import com.yourname.tomorrowlandshop.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/products")
    @ResponseBody
    public ResponseEntity<Void> listProducts() { return ResponseEntity.ok().build(); }

    @PostMapping("/products")
    @ResponseBody
    public ResponseEntity<Void> createProduct() { return ResponseEntity.ok().build(); }

    @PutMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<Void> updateProduct(@PathVariable Long id) { return ResponseEntity.ok().build(); }

    @DeleteMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) { return ResponseEntity.noContent().build(); }

    @PostMapping("/categories")
    @ResponseBody
    public ResponseEntity<Void> createCategory() { return ResponseEntity.ok().build(); }

    @PutMapping("/categories/{id}")
    @ResponseBody
    public ResponseEntity<Void> updateCategory(@PathVariable Long id) { return ResponseEntity.ok().build(); }

    @DeleteMapping("/categories/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) { return ResponseEntity.noContent().build(); }

    @GetMapping("/orders")
    public String orders(@RequestParam(required = false) Long userId,
                         @RequestParam(required = false) String from,
                         @RequestParam(required = false) String to) {
        adminService.toString();
        return "admin/orders";
    }

    @GetMapping("/audit-log")
    public String auditLog() { return "admin/audit-log"; }
}
