package com.yourname.tomorrowlandshop.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<Long, CartItem> itemsByProductId = new LinkedHashMap<>();

    public void addItem(Product product, int quantity) {
        addItem(product.getId(), product.getName(), product.getPrice(), quantity);
    }

    public void addItem(Long productId, String productName, BigDecimal price, int quantity) {
        if (quantity <= 0) {
            return;
        }
        CartItem existing = itemsByProductId.get(productId);
        if (existing == null) {
            itemsByProductId.put(productId, new CartItem(productId, productName, price, quantity));
        } else {
            existing.setQuantity(existing.getQuantity() + quantity);
        }
    }

    public void updateQuantity(Long productId, int quantity) {
        CartItem item = itemsByProductId.get(productId);
        if (item == null) {
            return;
        }
        if (quantity <= 0) {
            itemsByProductId.remove(productId);
        } else {
            item.setQuantity(quantity);
        }
    }

    public void removeItem(Long productId) {
        itemsByProductId.remove(productId);
    }

    public void clear() {
        itemsByProductId.clear();
    }

    public BigDecimal getTotal() {
        return itemsByProductId.values().stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(new ArrayList<>(itemsByProductId.values()));
    }

    public boolean isEmpty() {
        return itemsByProductId.isEmpty();
    }
}
