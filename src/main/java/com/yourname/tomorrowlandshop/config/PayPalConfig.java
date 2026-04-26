package com.yourname.tomorrowlandshop.config;

import com.paypal.sdk.Environment;
import com.paypal.sdk.PaypalServerSDKClient;
import com.paypal.sdk.authentication.ClientCredentialsAuthModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayPalConfig {

    @Value("${paypal.client-id:}")
    private String clientId;

    @Value("${paypal.client-secret:}")
    private String clientSecret;

    @Value("${paypal.mode:sandbox}")
    private String mode;

    @Bean
    public PaypalServerSDKClient paypalClient() {
        return new PaypalServerSDKClient.Builder()
                .clientCredentialsAuth(
                        new ClientCredentialsAuthModel.Builder(clientId, clientSecret).build()
                )
                .environment("live".equals(mode) ? Environment.PRODUCTION : Environment.SANDBOX)
                .build();
    }
}
