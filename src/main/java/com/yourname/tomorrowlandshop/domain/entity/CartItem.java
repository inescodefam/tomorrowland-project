package com.yourname.tomorrowlandshop.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long productId;
    private String productName;
    private BigDecimal price;
    private int quantity;

    public CartItem(Long productId, String productName, BigDecimal price, int quantity) {
        this.productId = Objects.requireNonNull(productId);
        this.productName = Objects.requireNonNull(productName);
        this.price = Objects.requireNonNull(price);
        this.quantity = quantity;
    }

    public CartItem(Product product, int quantity) {
        this(product.getId(), product.getName(), product.getPrice(), quantity);
    }

    public BigDecimal getLineTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
