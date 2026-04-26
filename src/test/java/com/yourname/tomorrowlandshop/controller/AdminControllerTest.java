package com.yourname.tomorrowlandshop.controller;

import com.yourname.tomorrowlandshop.config.PasswordConfig;
import com.yourname.tomorrowlandshop.repository.LoginAuditRepository;
import com.yourname.tomorrowlandshop.repository.UserRepository;
import com.yourname.tomorrowlandshop.security.CustomUserDetailsService;
import com.yourname.tomorrowlandshop.security.SecurityConfig;
import com.yourname.tomorrowlandshop.domain.entity.Product;
import com.yourname.tomorrowlandshop.service.AdminService;
import com.yourname.tomorrowlandshop.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import({SecurityConfig.class, PasswordConfig.class, CustomUserDetailsService.class})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AdminService adminService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private LoginAuditRepository loginAuditRepository;
    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void stubAdminData() {
        when(adminService.getAllProducts()).thenReturn(List.of());
        when(adminService.getAllCategories()).thenReturn(List.of());
        when(adminService.getProduct(1L)).thenReturn(Product.builder().id(1L).name("P").description("").price(BigDecimal.ONE).stock(1).build());
        when(adminService.getAllOrders(any(), any(), any())).thenReturn(List.of());
        when(adminService.getLoginAuditLog()).thenReturn(List.of());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminHasCrudAndReportingEndpoints() throws Exception {
        mockMvc.perform(get("/admin/products")).andExpect(status().isOk());
        mockMvc.perform(post("/admin/products").with(csrf())
                        .param("name", "Test")
                        .param("description", "D")
                        .param("price", "9.99")
                        .param("stock", "1")
                        .param("categoryId", "1"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/admin/products/1/edit")).andExpect(status().isOk());
        mockMvc.perform(post("/admin/products/1").with(csrf())
                        .param("name", "Test")
                        .param("description", "D")
                        .param("price", "9.99")
                        .param("stock", "1")
                        .param("categoryId", "1"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(post("/admin/products/1/delete").with(csrf())).andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/admin/categories")).andExpect(status().isOk());
        mockMvc.perform(post("/admin/categories").with(csrf()).param("name", "Cat").param("description", "D"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(post("/admin/categories/1/delete").with(csrf())).andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/admin/orders")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/audit-log")).andExpect(status().isOk());
    }
}
