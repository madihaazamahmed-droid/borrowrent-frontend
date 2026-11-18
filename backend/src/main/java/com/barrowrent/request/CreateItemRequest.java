package com.barrowrent.request;

import java.math.BigDecimal;

import com.barrowrent.enums.ItemCondition;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateItemRequest(

    @NotBlank(message = "Item name is required")
    @Size(max = 120, message = "Item name cannot exceed 120 characters")
    String name,

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    String description,

    @NotNull(message = "Daily rent is required")
    @DecimalMin(value = "0.01",
        message = "Daily rent must be greater than zero")
    BigDecimal dailyRent,

    @NotNull(message = "Item condition is required")
    ItemCondition condition,

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    String imageUrl,

    @NotBlank(message = "Location is required")
    @Size(max = 200, message = "Location cannot exceed 200 characters")
    String location,

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive")
    Long categoryId

) {
}