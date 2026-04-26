package com.yourname.tomorrowlandshop.controller;

import com.yourname.tomorrowlandshop.config.PasswordConfig;
import com.yourname.tomorrowlandshop.domain.entity.Product;
import com.yourname.tomorrowlandshop.repository.LoginAuditRepository;
import com.yourname.tomorrowlandshop.repository.UserRepository;
import com.yourname.tomorrowlandshop.security.CustomUserDetailsService;
import com.yourname.tomorrowlandshop.security.SecurityConfig;
import com.yourname.tomorrowlandshop.service.CartService;
import com.yourname.tomorrowlandshop.service.JwtService;
import com.yourname.tomorrowlandshop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, PasswordConfig.class, CustomUserDetailsService.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductService productService;
    @MockBean
    private CartService cartService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private LoginAuditRepository loginAuditRepository;
    @MockBean
    private UserRepository userRepository;

    @Test
    void shouldGetProductsAndDetails() throws Exception {
        when(productService.getAll()).thenReturn(List.of());
        when(productService.getById(1L)).thenReturn(Product.builder().id(1L).name("P").build());
        mockMvc.perform(get("/products")).andExpect(status().isOk());
        mockMvc.perform(get("/products/1")).andExpect(status().isOk());
    }
}
