package com.yourname.tomorrowlandshop.service;

import com.paypal.sdk.PaypalServerSDKClient;
import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.models.AmountWithBreakdown;
import com.paypal.sdk.models.OrdersCaptureInput;
import com.paypal.sdk.models.CheckoutPaymentIntent;
import com.paypal.sdk.models.OrdersCreateInput;
import com.paypal.sdk.models.Order;
import com.paypal.sdk.models.OrderRequest;
import com.paypal.sdk.models.PurchaseUnitRequest;
import com.yourname.tomorrowlandshop.domain.exception.PaymentFailedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PayPalServiceImpl implements PayPalService {

    private final OrdersController ordersController;

    public PayPalServiceImpl(PaypalServerSDKClient paypalClient) {
        this.ordersController = paypalClient.getOrdersController();
    }

    @Override
    public String createOrder(BigDecimal amount) throws PaymentFailedException {
        try {
            BigDecimal safeAmount = amount != null ? amount : BigDecimal.ZERO;
            OrderRequest orderRequest = new OrderRequest.Builder(
                    CheckoutPaymentIntent.CAPTURE,
                    List.of(
                            new PurchaseUnitRequest.Builder(
                                    new AmountWithBreakdown.Builder(
                                            "EUR",
                                            safeAmount.setScale(2, RoundingMode.HALF_UP).toPlainString()
                                    ).build()
                            ).build()
                    )
            ).build();

            Order order = ordersController
                    .ordersCreate(
                            new OrdersCreateInput.Builder()
                                    .body(orderRequest)
                                    .build()
                    )
                    .getResult();

            return order.getId();
        } catch (Exception e) {
            throw new PaymentFailedException("Failed to create PayPal order: " + e.getMessage());
        }
    }

    @Override
    public String captureOrder(String paypalOrderId) throws PaymentFailedException {
        try {
            Order order = ordersController
                    .ordersCapture(
                            new OrdersCaptureInput.Builder()
                                    .id(paypalOrderId)
                                    .build()
                    )
                    .getResult();

            String status = order.getStatus().toString();
            if (!"COMPLETED".equals(status)) {
                throw new PaymentFailedException("PayPal capture failed: " + status);
            }
            return status;
        } catch (PaymentFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new PaymentFailedException("Failed to capture PayPal order: " + e.getMessage());
        }
    }
}
