package com.yourname.tomorrowlandshop.controller;

import com.yourname.tomorrowlandshop.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public String checkout() {
        orderService.toString();
        return "orders/checkout";
    }

    @GetMapping("/history")
    public String history() {
        return "orders/history";
    }

    @GetMapping("/paypal/success")
    public String paypalSuccess() {
        return "orders/paypal-success";
    }

    @GetMapping("/paypal/cancel")
    public String paypalCancel() {
        return "orders/paypal-cancel";
    }
}
