package com.yourname.tomorrowlandshop.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void anonymousCanAccessPublicEndpoints() throws Exception {
        mockMvc.perform(get("/products")).andExpect(status().isOk());
        mockMvc.perform(get("/categories")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotAccessAdmin() throws Exception {
        mockMvc.perform(get("/admin/products")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanAccessAdmin() throws Exception {
        mockMvc.perform(get("/admin/products")).andExpect(status().isOk());
    }
}
