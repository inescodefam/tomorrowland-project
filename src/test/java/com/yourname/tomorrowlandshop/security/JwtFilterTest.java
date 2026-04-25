package com.yourname.tomorrowlandshop.security;

import com.yourname.tomorrowlandshop.controller.OrderController;
import com.yourname.tomorrowlandshop.controller.ProductController;
import com.yourname.tomorrowlandshop.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {OrderController.class, ProductController.class})
@Import(SecurityConfig.class)
class JwtFilterTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtService jwtService;

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
