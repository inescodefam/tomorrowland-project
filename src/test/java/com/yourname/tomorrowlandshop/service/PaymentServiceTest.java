package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.enums.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaypalGateway paypalGateway;
    @Mock
    private PayPalService payPalService;
    @InjectMocks
    private PaymentService paymentService;

    @Test
    void shouldHandleSuccessFailureAndCancel() {
        Order order = Order.builder().id(1L).build();
        when(paypalGateway.createPayment(order)).thenReturn("APPROVED");
        assertThat(paymentService.initiatePayment(order)).isEqualTo(PaymentStatus.SUCCESS);

        when(paypalGateway.createPayment(order)).thenReturn("FAILED");
        assertThat(paymentService.initiatePayment(order)).isEqualTo(PaymentStatus.FAILED);

        when(paypalGateway.createPayment(order)).thenReturn("CANCELED");
        assertThat(paymentService.initiatePayment(order)).isEqualTo(PaymentStatus.CANCELED);
    }
}
