package com.yourname.tomorrowlandshop.config;

import com.yourname.tomorrowlandshop.domain.entity.Cart;
import com.yourname.tomorrowlandshop.domain.entity.CartItem;
import com.yourname.tomorrowlandshop.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute
    public void cartItemCount(HttpSession session, Model model) {
        Object raw = session.getAttribute(CartService.SESSION_CART_KEY);
        int count = 0;
        if (raw instanceof Cart cart) {
            count = cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();
        }
        model.addAttribute("cartItemCount", count);
    }
}
