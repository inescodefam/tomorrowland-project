package com.yourname.tomorrowlandshop.controller;

import com.yourname.tomorrowlandshop.config.PasswordConfig;
import com.yourname.tomorrowlandshop.repository.LoginAuditRepository;
import com.yourname.tomorrowlandshop.repository.UserRepository;
import com.yourname.tomorrowlandshop.security.CustomUserDetailsService;
import com.yourname.tomorrowlandshop.security.SecurityConfig;
import com.yourname.tomorrowlandshop.domain.entity.Cart;
import com.yourname.tomorrowlandshop.service.CartService;
import com.yourname.tomorrowlandshop.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@Import({SecurityConfig.class, PasswordConfig.class, CustomUserDetailsService.class})
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CartService cartService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private LoginAuditRepository loginAuditRepository;
    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void stubCart() {
        when(cartService.getCart(any())).thenReturn(new Cart());
        when(cartService.calculateTotal(any())).thenReturn(BigDecimal.ZERO);
    }

    @Test
    void shouldAddUpdateRemoveAndClear() throws Exception {
        mockMvc.perform(post("/cart/add").with(csrf()).param("productId", "1").param("quantity", "1"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(put("/cart/update").with(csrf()).param("productId", "1").param("quantity", "2"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(delete("/cart/remove").with(csrf()).param("productId", "1"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(delete("/cart/clear").with(csrf())).andExpect(status().is3xxRedirection());
    }

    @Test
    void shouldShowCart() throws Exception {
        mockMvc.perform(get("/cart")).andExpect(status().isOk());
    }
}
