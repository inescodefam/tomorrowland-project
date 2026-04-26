package com.yourname.tomorrowlandshop.controller;

import com.yourname.tomorrowlandshop.domain.entity.Cart;
import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.entity.User;
import com.yourname.tomorrowlandshop.domain.enums.PaymentMethod;
import com.yourname.tomorrowlandshop.domain.enums.PaymentStatus;
import com.yourname.tomorrowlandshop.repository.UserRepository;
import com.yourname.tomorrowlandshop.service.CartService;
import com.yourname.tomorrowlandshop.service.OrderService;
import com.yourname.tomorrowlandshop.service.PayPalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private static final String REDIRECT_CART = "redirect:/cart";
    private static final String CHECKOUT_ERROR_ATTRIBUTE = "checkoutError";
    private static final String PAYPAL_ORDER_ID_ATTRIBUTE = "pendingPaypalOrderId";
    private static final String STOCK_UPDATED_MESSAGE =
            "Items in your cart are no longer in stock. Your cart has been updated.";

    private final OrderService orderService;
    private final CartService cartService;
    private final PayPalService payPalService;
    private final UserRepository userRepository;

    @Value("${paypal.client-id:}")
    private String paypalClientId;

    public OrderController(OrderService orderService, CartService cartService, PayPalService payPalService,
                           UserRepository userRepository) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.payPalService = payPalService;
        this.userRepository = userRepository;
    }

    @GetMapping("/checkout")
    public String checkoutForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        cartService.reconcileCartWithStock(session);
        Cart cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            redirectAttributes.addFlashAttribute(CHECKOUT_ERROR_ATTRIBUTE, "Your cart is empty");
            return REDIRECT_CART;
        }
        model.addAttribute("cart", cart);
        model.addAttribute("total", cartService.calculateTotal(session));
        model.addAttribute("paypalClientId", paypalClientId);
        return "orders/checkout";
    }

    @PostMapping("/checkout")
    public String checkoutPost(@RequestParam PaymentMethod paymentMethod,
                               HttpSession session,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        cartService.reconcileCartWithStock(session);
        Cart cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            redirectAttributes.addFlashAttribute(CHECKOUT_ERROR_ATTRIBUTE, STOCK_UPDATED_MESSAGE);
            return REDIRECT_CART;
        }
        if (paymentMethod == PaymentMethod.CASH_ON_DELIVERY) {
            Order order = orderService.placeOrder(user.getId(), cart, PaymentMethod.CASH_ON_DELIVERY);
            cartService.clearCart(session);
            return "redirect:/orders/confirmation?orderId=" + order.getId();
        }
        return REDIRECT_CART;
    }

    @PostMapping("/paypal/create")
    @ResponseBody
    public ResponseEntity<Map<String, String>> createPayPalOrder(HttpSession session) {
        try {
            cartService.reconcileCartWithStock(session);
            Cart cart = cartService.getCart(session);
            if (cart.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cart is empty"));
            }
            BigDecimal total = cartService.calculateTotal(session);
            String paypalOrderId = payPalService.createOrder(total);
            session.setAttribute(PAYPAL_ORDER_ID_ATTRIBUTE, paypalOrderId);
            return ResponseEntity.ok(Map.of("paypalOrderId", paypalOrderId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Could not create PayPal order"));
        }
    }

    @PostMapping("/paypal/capture")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> capturePayPalOrder(@RequestParam String paypalOrderId,
                                                                   @AuthenticationPrincipal UserDetails userDetails,
                                                                   HttpSession session) {
        try {
            String expected = (String) session.getAttribute(PAYPAL_ORDER_ID_ATTRIBUTE);
            if (expected != null && !expected.equals(paypalOrderId)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid PayPal order id"));
            }
            payPalService.captureOrder(paypalOrderId);
            cartService.reconcileCartWithStock(session);
            Cart cart = cartService.getCart(session);
            if (cart.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cart is empty"));
            }
            User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            Order order = orderService.placeOrder(user.getId(), cart, PaymentMethod.PAYPAL);
            orderService.confirmPayment(order.getId(), paypalOrderId);
            cartService.clearCart(session);
            session.removeAttribute(PAYPAL_ORDER_ID_ATTRIBUTE);
            return ResponseEntity.ok(Map.of("orderId", order.getId(), "status", PaymentStatus.SUCCESS.name()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Payment capture failed"));
        }
    }

    @GetMapping("/paypal/cancel")
    public String paypalCancel(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("paypalMessage", "PayPal payment cancelled");
        return REDIRECT_CART;
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
