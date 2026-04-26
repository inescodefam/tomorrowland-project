package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Product;
import com.yourname.tomorrowlandshop.domain.exception.NotFoundException;
import com.yourname.tomorrowlandshop.repository.CategoryRepository;
import com.yourname.tomorrowlandshop.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private ProductService productService;

    @Test
    void getAll_shouldReturnProducts() {
        when(productRepository.findAll()).thenReturn(List.of(Product.builder().id(1L).build()));
        assertThat(productService.getAll()).hasSize(1);
    }

    @Test
    void getById_shouldThrowWhenMissing() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.getById(999L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("create update delete delegates to repository")
    void shouldCreateUpdateDelete() {
        Product product = Product.builder().id(1L).build();
        when(productRepository.save(product)).thenReturn(product);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.create(product);
        productService.update(1L, product);
        productService.delete(1L);

        verify(productRepository).save(product);
        verify(productRepository).delete(product);
    }
}
