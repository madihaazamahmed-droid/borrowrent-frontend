package com.barrowrent.request;

import java.math.BigDecimal;

import com.barrowrent.enums.ItemCondition;
import com.barrowrent.enums.ItemStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateItemRequest(

        @NotBlank(message = "Item name is required")
        @Size(max = 120)
        String name,

        @NotBlank(message = "Description is required")
        @Size(max = 2000)
        String description,

        @NotNull(message = "Daily rental price is required")
        @DecimalMin(value = "0.00", message = "Rental price cannot be negative")
        BigDecimal dailyRent,

        @NotNull(message = "Item condition is required")
        ItemCondition condition,

        @Size(max = 500)
        String imageUrl,

        @NotBlank(message = "Location is required")
        @Size(max = 200)
        String location,

        @NotNull(message = "Category ID is required")
        @Positive(message = "Category ID must be positive")
        Long categoryId,

        @NotNull(message = "Item status is required")
        ItemStatus status
) {
}