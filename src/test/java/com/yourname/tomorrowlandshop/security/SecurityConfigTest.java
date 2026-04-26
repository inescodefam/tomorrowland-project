package com.yourname.tomorrowlandshop.security;

import com.yourname.tomorrowlandshop.controller.AdminController;
import com.yourname.tomorrowlandshop.controller.CategoryController;
import com.yourname.tomorrowlandshop.controller.ProductController;
import com.yourname.tomorrowlandshop.repository.LoginAuditRepository;
import com.yourname.tomorrowlandshop.service.AdminService;
import com.yourname.tomorrowlandshop.service.JwtService;
import com.yourname.tomorrowlandshop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AdminController.class, ProductController.class, CategoryController.class})
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private LoginAuditRepository loginAuditRepository;
    @MockBean
    private AdminService adminService;
    @MockBean
    private ProductService productService;

    @Test
    void anonymousCanAccessPublicEndpoints() throws Exception {
        mockMvc.perform(get("/products")).andExpect(status().isOk());
        mockMvc.perform(get("/categories")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotAccessAdmin() throws Exception {
        mockMvc.perform(get("/admin/products"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/products"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanAccessAdmin() throws Exception {
        mockMvc.perform(get("/admin/products")).andExpect(status().isOk());
    }
}
