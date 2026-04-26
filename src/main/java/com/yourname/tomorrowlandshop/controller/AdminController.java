package com.yourname.tomorrowlandshop.controller;

import com.yourname.tomorrowlandshop.dto.CategoryDto;
import com.yourname.tomorrowlandshop.dto.ProductDto;
import com.yourname.tomorrowlandshop.service.AdminService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", adminService.getAllProducts());
        model.addAttribute("categories", adminService.getAllCategories());
        return "admin/products";
    }

    @PostMapping("/products")
    public String createProduct(@ModelAttribute ProductDto dto) {
        adminService.createProduct(dto);
        return "redirect:/admin/products";
    }

    @GetMapping("/products/{id}/edit")
    public String editProduct(@PathVariable Long id, Model model) {
        model.addAttribute("product", adminService.getProduct(id));
        model.addAttribute("categories", adminService.getAllCategories());
        return "admin/products-edit";
    }

    @PostMapping("/products/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute ProductDto dto) {
        adminService.updateProduct(id, dto);
        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", adminService.getAllCategories());
        return "admin/categories";
    }

    @PostMapping("/categories")
    public String createCategory(@ModelAttribute CategoryDto dto) {
        adminService.createCategory(dto);
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id) {
        adminService.deleteCategory(id);
        return "redirect:/admin/categories";
    }

    @GetMapping("/orders")
    public String orders(@RequestParam(required = false) String username,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                         Model model) {
        model.addAttribute("orders", adminService.getAllOrders(username, from, to));
        return "admin/orders";
    }

    @GetMapping("/audit-log")
    public String auditLog(Model model) {
        model.addAttribute("auditLog", adminService.getLoginAuditLog());
        return "admin/audit-log";
    }
}
