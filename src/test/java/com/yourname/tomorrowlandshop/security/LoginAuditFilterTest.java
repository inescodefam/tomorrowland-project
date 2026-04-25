package com.yourname.tomorrowlandshop.security;

import com.yourname.tomorrowlandshop.repository.LoginAuditRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(SecurityConfig.class)
class LoginAuditFilterTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LoginAuditRepository loginAuditRepository;

    @Test
    @WithMockUser(username = "user1")
    void successfulLoginWritesAuditRecord() throws Exception {
        mockMvc.perform(post("/login").with(req -> { req.setRemoteAddr("127.0.0.1"); return req; }))
                .andExpect(status().is3xxRedirection());

        ArgumentCaptor<com.yourname.tomorrowlandshop.domain.entity.LoginAudit> captor =
                ArgumentCaptor.forClass(com.yourname.tomorrowlandshop.domain.entity.LoginAudit.class);
        verify(loginAuditRepository).save(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("user1");
        assertThat(captor.getValue().getIpAddress()).isEqualTo("127.0.0.1");
        assertThat(captor.getValue().getLoginAt()).isNotNull();
    }
}
