package com.yourname.tomorrowlandshop.controller;

import com.yourname.tomorrowlandshop.config.CheckoutExceptionHandler;
import com.yourname.tomorrowlandshop.config.PasswordConfig;
import com.yourname.tomorrowlandshop.domain.entity.Cart;
import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.entity.Product;
import com.yourname.tomorrowlandshop.domain.entity.User;
import com.yourname.tomorrowlandshop.domain.enums.PaymentMethod;
import com.yourname.tomorrowlandshop.domain.enums.Role;
import com.yourname.tomorrowlandshop.domain.exception.InsufficientStockException;
import com.yourname.tomorrowlandshop.repository.LoginAuditRepository;
import com.yourname.tomorrowlandshop.repository.UserRepository;
import com.yourname.tomorrowlandshop.security.CustomUserDetailsService;
import com.yourname.tomorrowlandshop.security.SecurityConfig;
import com.yourname.tomorrowlandshop.service.CartService;
import com.yourname.tomorrowlandshop.service.JwtService;
import com.yourname.tomorrowlandshop.service.OrderService;
import com.yourname.tomorrowlandshop.service.PaymentService;
import com.yourname.tomorrowlandshop.service.PayPalService;
import com.yourname.tomorrowlandshop.service.PayPalCheckoutStart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import({SecurityConfig.class, PasswordConfig.class, CustomUserDetailsService.class, CheckoutExceptionHandler.class})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OrderService orderService;
    @MockBean
    private CartService cartService;
    @MockBean
    private PaymentService paymentService;
    @MockBean
    private PayPalService payPalService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private LoginAuditRepository loginAuditRepository;

    @Test
    @WithMockUser(roles = "USER")
    void userCanCheckoutAndViewHistory() throws Exception {
        User user = User.builder().id(7L).username("user").role(Role.ROLE_USER).build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        Cart cart = new Cart();
        cart.addItem(Product.builder().id(1L).name("x").price(BigDecimal.TEN).build(), 1);
        when(cartService.getCart(any())).thenReturn(cart);
        when(cartService.calculateTotal(any())).thenReturn(BigDecimal.TEN);
        when(orderService.placeOrder(anyLong(), any(Cart.class), any(PaymentMethod.class)))
                .thenReturn(Order.builder().id(5L).build());

        mockMvc.perform(get("/orders/checkout")).andExpect(status().isOk());
        mockMvc.perform(post("/orders/checkout").with(csrf()).param("paymentMethod", "CASH_ON_DELIVERY"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/orders/history")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCanStartPayPalCheckout() throws Exception {
        User user = User.builder().id(7L).username("user").role(Role.ROLE_USER).build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        Cart cart = new Cart();
        cart.addItem(Product.builder().id(1L).name("x").price(BigDecimal.TEN).build(), 1);
        when(cartService.getCart(any())).thenReturn(cart);
        when(cartService.calculateTotal(any())).thenReturn(BigDecimal.TEN);
        when(paymentService.createPayPalCheckout(any(BigDecimal.class)))
                .thenReturn(new PayPalCheckoutStart("https://paypal.test/approve", "ORDER-XYZ"));

        mockMvc.perform(post("/orders/checkout").with(csrf()).param("paymentMethod", "PAYPAL"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "USER")
    void checkoutRedirectsToCartWhenOutOfStock() throws Exception {
        User user = User.builder().id(7L).username("user").role(Role.ROLE_USER).build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        Cart cart = new Cart();
        cart.addItem(Product.builder().id(1L).name("x").price(BigDecimal.TEN).build(), 1);
        when(cartService.getCart(any())).thenReturn(cart);
        doThrow(new InsufficientStockException("sold out")).when(orderService)
                .placeOrder(eq(7L), any(Cart.class), eq(PaymentMethod.CASH_ON_DELIVERY));

        mockMvc.perform(post("/orders/checkout").with(csrf()).param("paymentMethod", "CASH_ON_DELIVERY"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }
}
