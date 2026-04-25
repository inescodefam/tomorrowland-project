package com.yourname.tomorrowlandshop.controller;

import com.yourname.tomorrowlandshop.repository.LoginAuditRepository;
import com.yourname.tomorrowlandshop.security.SecurityConfig;
import com.yourname.tomorrowlandshop.service.AdminService;
import com.yourname.tomorrowlandshop.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AdminService adminService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private LoginAuditRepository loginAuditRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminHasCrudAndReportingEndpoints() throws Exception {
        mockMvc.perform(post("/admin/products")).andExpect(status().isOk());
        mockMvc.perform(put("/admin/products/1")).andExpect(status().isOk());
        mockMvc.perform(delete("/admin/products/1")).andExpect(status().isNoContent());
        mockMvc.perform(post("/admin/categories")).andExpect(status().isOk());
        mockMvc.perform(put("/admin/categories/1")).andExpect(status().isOk());
        mockMvc.perform(delete("/admin/categories/1")).andExpect(status().isNoContent());
        mockMvc.perform(get("/admin/orders")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/audit-log")).andExpect(status().isOk());
    }
}
