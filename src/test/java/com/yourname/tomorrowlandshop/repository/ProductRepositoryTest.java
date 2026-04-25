package com.yourname.tomorrowlandshop.repository;

import com.yourname.tomorrowlandshop.domain.entity.Category;
import com.yourname.tomorrowlandshop.domain.entity.Product;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("find products by category id")
    void shouldFindByCategory() {
        Category category = categoryRepository.save(Category.builder().name("Tickets").build());
        productRepository.save(Product.builder().name("Pass").price(new BigDecimal("1.00")).stock(1).category(category).build());

        assertThat(productRepository.findByCategory(category)).hasSize(1);
    }

    @Test
    @DisplayName("find product by id with lock")
    void shouldFindByIdWithLock() {
        Product product = productRepository.save(Product.builder().name("Pass").price(new BigDecimal("2.00")).stock(5).build());

        assertThat(productRepository.findByIdWithPessimisticLock(product.getId())).isPresent();
    }

    @Test
    @DisplayName("optimistic lock conflict simulation")
    void shouldFailOnOptimisticConflict() {
        Product created = productRepository.saveAndFlush(Product.builder().name("Pass").price(new BigDecimal("3.00")).stock(5).build());
        Product staleCopy = productRepository.findById(created.getId()).orElseThrow();
        entityManager.detach(staleCopy);

        Product updated = productRepository.findById(created.getId()).orElseThrow();
        updated.setStock(4);
        productRepository.saveAndFlush(updated);

        staleCopy.setStock(3);
        assertThatThrownBy(() -> productRepository.saveAndFlush(staleCopy))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }
}
