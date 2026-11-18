package com.barrowrent.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.barrowrent.enums.ItemCondition;
import com.barrowrent.enums.ItemStatus;

public record ItemResponse(

    Long id,
    String name,
    String description,
    BigDecimal dailyRent,
    ItemCondition condition,
    ItemStatus status,
    String imageUrl,
    String location,
    Long ownerId,
    Long categoryId,
    String categoryName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

) {
}