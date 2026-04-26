package com.yourname.tomorrowlandshop.controller;

import com.yourname.tomorrowlandshop.domain.entity.Cart;
import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.entity.User;
import com.yourname.tomorrowlandshop.domain.enums.PaymentMethod;
import com.yourname.tomorrowlandshop.domain.enums.PaymentStatus;
import com.yourname.tomorrowlandshop.repository.UserRepository;
import com.yourname.tomorrowlandshop.service.CartService;
import com.yourname.tomorrowlandshop.service.OrderService;
import com.yourname.tomorrowlandshop.service.PaymentService;
import com.yourname.tomorrowlandshop.service.PayPalCheckoutStart;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final PaymentService paymentService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, CartService cartService, PaymentService paymentService,
                           UserRepository userRepository) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/checkout")
    public String checkoutForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Cart cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("checkoutError", "Your cart is empty");
            return "redirect:/cart";
        }
        model.addAttribute("cart", cart);
        model.addAttribute("total", cartService.calculateTotal(session));
        return "orders/checkout";
    }

    @PostMapping("/checkout")
    public String checkoutPost(@RequestParam PaymentMethod paymentMethod,
                               HttpSession session,
                               Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        Cart cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }
        if (paymentMethod == PaymentMethod.CASH_ON_DELIVERY) {
            Order order = orderService.placeOrder(user.getId(), cart, PaymentMethod.CASH_ON_DELIVERY);
            cartService.clearCart(session);
            return "redirect:/orders/confirmation?orderId=" + order.getId();
        }
        BigDecimal total = cartService.calculateTotal(session);
        PayPalCheckoutStart start = paymentService.createPayPalCheckout(total);
        session.setAttribute("paypalOrderId", start.paypalOrderId());
        return "redirect:" + start.approvalUrl();
    }

    @GetMapping("/paypal/success")
    public String paypalSuccess(@RequestParam String paypalOrderId,
                                HttpSession session,
                                Authentication authentication) {
        String expected = (String) session.getAttribute("paypalOrderId");
        if (expected != null && !expected.equals(paypalOrderId)) {
            return "redirect:/cart";
        }
        if (paymentService.capturePayPalOrder(paypalOrderId) != PaymentStatus.SUCCESS) {
            return "redirect:/cart";
        }
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        Cart cart = cartService.getCart(session);
        Order order = orderService.placeOrder(user.getId(), cart, PaymentMethod.PAYPAL);
        orderService.confirmPayment(order.getId(), paypalOrderId);
        cartService.clearCart(session);
        session.removeAttribute("paypalOrderId");
        return "redirect:/orders/confirmation?orderId=" + order.getId();
    }

    @GetMapping("/paypal/cancel")
    public String paypalCancel(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("paypalMessage", "PayPal payment cancelled");
        return "redirect:/cart";
    }

    @GetMapping("/confirmation")
    public String confirmation(@RequestParam Long orderId, Model model) {
        model.addAttribute("order", orderService.getById(orderId));
        return "orders/confirmation";
    }

    @GetMapping("/history")
    public String history(Authentication authentication, Model model) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        model.addAttribute("orders", orderService.getByUser(user.getId()));
        return "orders/history";
    }
}
