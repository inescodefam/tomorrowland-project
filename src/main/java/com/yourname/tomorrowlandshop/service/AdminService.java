package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Category;
import com.yourname.tomorrowlandshop.domain.entity.LoginAudit;
import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.entity.Product;
import com.yourname.tomorrowlandshop.domain.exception.NotFoundException;
import com.yourname.tomorrowlandshop.dto.CategoryDto;
import com.yourname.tomorrowlandshop.dto.ProductDto;
import com.yourname.tomorrowlandshop.repository.CategoryRepository;
import com.yourname.tomorrowlandshop.repository.LoginAuditRepository;
import com.yourname.tomorrowlandshop.repository.OrderRepository;
import com.yourname.tomorrowlandshop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {

    private static final String CATEGORY_NOT_FOUND = "Category not found";

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final LoginAuditRepository loginAuditRepository;

    public AdminService(ProductRepository productRepository, CategoryRepository categoryRepository,
                        OrderRepository orderRepository, LoginAuditRepository loginAuditRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
        this.loginAuditRepository = loginAuditRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProduct(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
    }

    @Transactional
    public void createProduct(ProductDto dto) {
        var category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND));
        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .category(category)
                .build();
        productRepository.save(product);
    }

    @Transactional
    public void updateProduct(Long id, ProductDto dto) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
        var category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND));
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setImageUrl(dto.getImageUrl());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategory(category);
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    public void createCategory(CategoryDto dto) {
        categoryRepository.save(Category.builder().name(dto.getName()).description(dto.getDescription()).build());
    }

    @Transactional
    public void updateCategory(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND));
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders(String username, LocalDateTime from, LocalDateTime to) {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(o -> username == null || username.isBlank()
                        || o.getUser().getUsername().toLowerCase().contains(username.strip().toLowerCase()))
                .filter(o -> from == null || !o.getCreatedAt().isBefore(from))
                .filter(o -> to == null || !o.getCreatedAt().isAfter(to))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LoginAudit> getLoginAuditLog() {
        return loginAuditRepository.findAll();
    }
}
