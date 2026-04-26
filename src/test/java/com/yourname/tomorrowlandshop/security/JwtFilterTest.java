package com.yourname.tomorrowlandshop.security;

import com.yourname.tomorrowlandshop.config.PasswordConfig;
import com.yourname.tomorrowlandshop.controller.OrderController;
import com.yourname.tomorrowlandshop.controller.ProductController;
import com.yourname.tomorrowlandshop.repository.LoginAuditRepository;
import com.yourname.tomorrowlandshop.repository.UserRepository;
import com.yourname.tomorrowlandshop.service.CartService;
import com.yourname.tomorrowlandshop.service.JwtService;
import com.yourname.tomorrowlandshop.service.OrderService;
import com.yourname.tomorrowlandshop.domain.entity.User;
import com.yourname.tomorrowlandshop.domain.enums.Role;
import com.yourname.tomorrowlandshop.service.PaymentService;
import com.yourname.tomorrowlandshop.service.PayPalService;
import com.yourname.tomorrowlandshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {OrderController.class, ProductController.class})
@Import({SecurityConfig.class, PasswordConfig.class, CustomUserDetailsService.class})
class JwtFilterTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private ProductService productService;
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
    private LoginAuditRepository loginAuditRepository;

    @BeforeEach
    void stubs() {
        when(jwtService.isTokenExpired(anyString())).thenReturn(false);
        when(jwtService.extractUsername(anyString())).thenReturn("jwt-user");
        when(userRepository.findByUsername("jwt-user")).thenReturn(Optional.of(
                User.builder().id(1L).username("jwt-user").role(Role.ROLE_USER).build()));
        when(orderService.getByUser(anyLong())).thenReturn(List.of());
        when(productService.getAll()).thenReturn(List.of());
    }

    @Test
    void validTokenPasses() throws Exception {
        when(jwtService.validateToken(anyString())).thenReturn(true);
        mockMvc.perform(get("/orders/history").header("Authorization", "Bearer valid")).andExpect(status().isOk());
    }

    @Test
    void expiredOrMissingTokenRejectedAndAnonymousAllowed() throws Exception {
        when(jwtService.validateToken(anyString())).thenReturn(false);
        mockMvc.perform(get("/orders/history").header("Authorization", "Bearer expired")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/orders/history")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/products")).andExpect(status().isOk());
    }
}
