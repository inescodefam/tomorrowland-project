package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Cart;
import com.yourname.tomorrowlandshop.domain.entity.CartItem;
import com.yourname.tomorrowlandshop.domain.entity.Product;
import com.yourname.tomorrowlandshop.domain.exception.InsufficientStockException;
import com.yourname.tomorrowlandshop.domain.exception.NotFoundException;
import com.yourname.tomorrowlandshop.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    public static final String SESSION_CART_KEY = "cart";

    private final ProductRepository productRepository;

    public CartService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void addItem(HttpSession session, Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        if (product.getStock() <= 0) {
            throw new InsufficientStockException("Sold out");
        }
        if (quantity > product.getStock()) {
            throw new InsufficientStockException("Only " + product.getStock() + " item(s) left in stock");
        }
        getOrCreate(session).addItem(product, quantity);
    }

    public void updateQuantity(HttpSession session, Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        if (product.getStock() <= 0) {
            getOrCreate(session).removeItem(productId);
            throw new InsufficientStockException("Sold out");
        }
        if (quantity > product.getStock()) {
            getOrCreate(session).updateQuantity(productId, product.getStock());
            throw new InsufficientStockException("Quantity adjusted to available stock (" + product.getStock() + ")");
        }
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

    /**
     * Aligns session cart lines with live stock (removes sold-out lines, caps quantity to available stock).
     */
    public void reconcileCartWithStock(HttpSession session) {
        Cart cart = getOrCreate(session);
        List<CartItem> snapshot = new ArrayList<>(cart.getItems());
        for (CartItem item : snapshot) {
            productRepository.findById(item.getProductId()).ifPresentOrElse(product -> {
                if (product.getStock() <= 0) {
                    cart.removeItem(item.getProductId());
                } else if (product.getStock() < item.getQuantity()) {
                    cart.updateQuantity(item.getProductId(), product.getStock());
                }
            }, () -> cart.removeItem(item.getProductId()));
        }
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
