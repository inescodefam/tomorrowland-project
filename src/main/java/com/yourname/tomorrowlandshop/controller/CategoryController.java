package com.yourname.tomorrowlandshop.controller;

import com.yourname.tomorrowlandshop.service.CategoryService;
import com.yourname.tomorrowlandshop.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public CategoryController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.getAll());
        return "categories/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getById(id));
        model.addAttribute("products", productService.getByCategory(id));
        return "categories/detail";
    }
}
