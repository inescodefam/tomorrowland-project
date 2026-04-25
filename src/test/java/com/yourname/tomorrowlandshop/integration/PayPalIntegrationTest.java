package com.yourname.tomorrowlandshop.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.yourname.tomorrowlandshop.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PayPalIntegrationTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance().build();

    @Autowired
    private PaymentService paymentService;

    @Test
    void shouldCompleteSandboxFlowWithWireMock() {
        wireMock.stubFor(post(urlEqualTo("/v2/checkout/orders"))
                .willReturn(aResponse().withStatus(201).withBody("{\"id\":\"ORDER-123\",\"status\":\"APPROVED\"}")));

        String result = paymentService.createPayPalOrder(1L);

        assertThat(result).contains("ORDER-123");
    }
}
