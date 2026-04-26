package com.yourname.tomorrowlandshop.config;

import com.yourname.tomorrowlandshop.controller.OrderController;
import com.yourname.tomorrowlandshop.domain.exception.InsufficientStockException;
import com.yourname.tomorrowlandshop.domain.exception.OrderConflictException;
import com.yourname.tomorrowlandshop.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(assignableTypes = OrderController.class)
public class CheckoutExceptionHandler {

    private final CartService cartService;

    public CheckoutExceptionHandler(CartService cartService) {
        this.cartService = cartService;
    }

    @ExceptionHandler({InsufficientStockException.class, OrderConflictException.class})
    public String handleInventoryConflict(HttpSession session, RedirectAttributes redirectAttributes) {
        cartService.reconcileCartWithStock(session);
        redirectAttributes.addFlashAttribute("checkoutError",
                "This item is no longer available in that quantity — another customer may have purchased the last units. "
                        + "Your cart has been updated to match current stock.");
        return "redirect:/cart";
    }
}
