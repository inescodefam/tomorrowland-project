package com.yourname.tomorrowlandshop.controller;

import com.yourname.tomorrowlandshop.domain.exception.InsufficientStockException;
import com.yourname.tomorrowlandshop.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    private static final String REDIRECT_CART = "redirect:/cart";

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public String add(@RequestParam Long productId,
                      @RequestParam(defaultValue = "1") int quantity,
                      HttpSession session,
                      RedirectAttributes redirectAttributes) {
        try {
            cartService.addItem(session, productId, quantity);
        } catch (InsufficientStockException ex) {
            redirectAttributes.addFlashAttribute("checkoutError", ex.getMessage());
        }
        return REDIRECT_CART;
    }

    @GetMapping
    public String view(HttpSession session, Model model) {
        cartService.reconcileCartWithStock(session);
        model.addAttribute("cart", cartService.getCart(session));
        model.addAttribute("total", cartService.calculateTotal(session));
        return "cart/cart";
    }

    @PutMapping("/update")
    public String update(@RequestParam Long productId,
                         @RequestParam int quantity,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        try {
            cartService.updateQuantity(session, productId, quantity);
        } catch (InsufficientStockException ex) {
            redirectAttributes.addFlashAttribute("checkoutError", ex.getMessage());
        }
        return REDIRECT_CART;
    }

    @DeleteMapping("/remove")
    public String remove(@RequestParam Long productId, HttpSession session) {
        cartService.removeItem(session, productId);
        return REDIRECT_CART;
    }

    @DeleteMapping("/clear")
    public String clear(HttpSession session) {
        cartService.clearCart(session);
        return REDIRECT_CART;
    }
}
