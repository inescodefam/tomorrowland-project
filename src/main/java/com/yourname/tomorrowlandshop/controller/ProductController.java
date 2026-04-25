package com.yourname.tomorrowlandshop.controller;

import com.yourname.tomorrowlandshop.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String list() {
        productService.getAll();
        return "products/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id) {
        productService.getById(id);
        return "products/detail";
    }
}
