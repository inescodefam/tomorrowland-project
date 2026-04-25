package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Cart;
import com.yourname.tomorrowlandshop.domain.entity.Product;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartService {

    public void add(HttpSession session, Product product, int quantity) {
        addItem(session, product, quantity);
    }

    public void addItem(HttpSession session, Product product, int quantity) {
        getOrCreate(session).addItem(product, quantity);
    }

    public void update(HttpSession session, Long productId, int quantity) {
        updateQuantity(session, productId, quantity);
    }

    public void updateQuantity(HttpSession session, Long productId, int quantity) {
        getOrCreate(session).updateQuantity(productId, quantity);
    }

    public void remove(HttpSession session, Long productId) {
        removeItem(session, productId);
    }

    public void removeItem(HttpSession session, Long productId) {
        getOrCreate(session).removeItem(productId);
    }

    public void clear(HttpSession session) {
        clearCart(session);
    }

    public void clearCart(HttpSession session) {
        getOrCreate(session).clear();
    }

    public Cart getCart(HttpSession session) {
        return getOrCreate(session);
    }

    public BigDecimal calculateTotal(HttpSession session) {
        return getOrCreate(session).getTotal();
    }

    private Cart getOrCreate(HttpSession session) {
        Object cart = session.getAttribute("CART");
        if (cart instanceof Cart c) {
            return c;
        }
        Cart created = new Cart();
        session.setAttribute("CART", created);
        return created;
    }
}
