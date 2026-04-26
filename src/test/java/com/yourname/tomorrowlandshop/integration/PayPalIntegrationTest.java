package com.yourname.tomorrowlandshop.integration;

import com.yourname.tomorrowlandshop.service.PaymentService;
import com.yourname.tomorrowlandshop.service.PayPalCheckoutStart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PayPalIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void shouldCreateCheckoutSession() {
        PayPalCheckoutStart start = paymentService.createPayPalCheckout(new BigDecimal("49.99"));

        assertThat(start.approvalUrl()).isNotBlank();
        assertThat(start.paypalOrderId()).contains("ORDER");
    }
}
