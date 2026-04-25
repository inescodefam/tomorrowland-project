package com.yourname.tomorrowlandshop.controller;

import com.yourname.tomorrowlandshop.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Void> add(@RequestBody String payload) {
        if (payload == null) {
            cartService.toString();
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    @ResponseBody
    public ResponseEntity<Void> update(@RequestBody String payload) {
        if (payload == null) {
            cartService.toString();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    @ResponseBody
    public ResponseEntity<Void> remove(@RequestParam Long productId) {
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    @ResponseBody
    public ResponseEntity<Void> clear() {
        return ResponseEntity.noContent().build();
    }
}
