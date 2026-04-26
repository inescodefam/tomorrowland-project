package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Cart;
import com.yourname.tomorrowlandshop.domain.entity.Product;
import com.yourname.tomorrowlandshop.domain.exception.NotFoundException;
import com.yourname.tomorrowlandshop.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartService {

    public static final String SESSION_CART_KEY = "cart";

    private final ProductRepository productRepository;

    public CartService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void addItem(HttpSession session, Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        getOrCreate(session).addItem(product, quantity);
    }

    public void updateQuantity(HttpSession session, Long productId, int quantity) {
        getOrCreate(session).updateQuantity(productId, quantity);
    }

    public void removeItem(HttpSession session, Long productId) {
        getOrCreate(session).removeItem(productId);
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
        Object cart = session.getAttribute(SESSION_CART_KEY);
        if (cart instanceof Cart c) {
            return c;
        }
        Cart created = new Cart();
        session.setAttribute(SESSION_CART_KEY, created);
        return created;
    }
}
