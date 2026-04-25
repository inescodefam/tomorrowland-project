package com.yourname.tomorrowlandshop.domain.entity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Cart {

    private final Map<Long, CartItem> items = new HashMap<>();

    public void addItem(Product product, int quantity) {
        CartItem item = items.get(product.getId());
        if (item == null) {
            items.put(product.getId(), new CartItem(product, quantity));
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }
    }

    public void updateQuantity(Long productId, int quantity) {
        CartItem item = items.get(productId);
        if (item != null) {
            item.setQuantity(quantity);
        }
    }

    public void removeItem(Long productId) {
        items.remove(productId);
    }

    public void clear() {
        items.clear();
    }

    public BigDecimal getTotal() {
        return items.values().stream()
                .map(i -> i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<Long, CartItem> getItems() {
        return Collections.unmodifiableMap(items);
    }
}
