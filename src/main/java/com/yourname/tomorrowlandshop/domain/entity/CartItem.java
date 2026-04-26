package com.yourname.tomorrowlandshop.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long productId;
    private String productName;
    private BigDecimal price;
    private int quantity;

    public CartItem() {
    }

    public CartItem(Long productId, String productName, BigDecimal price, int quantity) {
        this.productId = Objects.requireNonNull(productId);
        this.productName = Objects.requireNonNull(productName);
        this.price = Objects.requireNonNull(price);
        this.quantity = quantity;
    }

    public CartItem(Product product, int quantity) {
        this(product.getId(), product.getName(), product.getPrice(), quantity);
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getLineTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
