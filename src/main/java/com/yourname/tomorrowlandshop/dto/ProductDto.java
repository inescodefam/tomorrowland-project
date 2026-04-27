package com.yourname.tomorrowlandshop.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDto {

    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private int stock;
    private Long categoryId;
}
