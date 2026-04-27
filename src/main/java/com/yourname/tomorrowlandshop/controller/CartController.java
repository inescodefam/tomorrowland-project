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
    private static final String CHECKOUT_ERROR = "checkoutError";
    private static final String PAYPAL_MESSAGE = "paypalMessage";

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
            redirectAttributes.addFlashAttribute(CHECKOUT_ERROR, ex.getMessage());
        }
        return REDIRECT_CART;
    }

    @GetMapping
    public String view(HttpSession session, Model model,
                       @RequestParam(required = false) String message) {
        cartService.reconcileCartWithStock(session);
        model.addAttribute("cart", cartService.getCart(session));
        model.addAttribute("total", cartService.calculateTotal(session));
        if ("paypal-cancelled".equals(message)) {
            model.addAttribute(PAYPAL_MESSAGE, "PayPal payment was cancelled.");
        } else if ("paypal-timeout".equals(message)) {
            model.addAttribute(PAYPAL_MESSAGE, "Payment window expired. Your reservation has been released.");
        } else if ("paypal-error".equals(message)) {
            model.addAttribute(PAYPAL_MESSAGE, "PayPal payment failed. Please try again.");
        } else if ("out-of-stock".equals(message)) {
            model.addAttribute(CHECKOUT_ERROR, "Sorry, this item sold out while you were paying. You have not been charged.");
        }
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
            redirectAttributes.addFlashAttribute(CHECKOUT_ERROR, ex.getMessage());
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
