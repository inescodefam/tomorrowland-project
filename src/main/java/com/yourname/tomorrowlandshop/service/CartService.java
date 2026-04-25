package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Cart;
import com.yourname.tomorrowlandshop.domain.entity.Product;
import jakarta.servlet.http.HttpSession;

public class CartService {

    public void add(HttpSession session, Product product, int quantity) {
        getOrCreate(session).addItem(product, quantity);
    }

    public void update(HttpSession session, Long productId, int quantity) {
        getOrCreate(session).updateQuantity(productId, quantity);
    }

    public void remove(HttpSession session, Long productId) {
        getOrCreate(session).removeItem(productId);
    }

    public void clear(HttpSession session) {
        getOrCreate(session).clear();
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
