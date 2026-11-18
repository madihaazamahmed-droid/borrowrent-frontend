package com.barrowrent.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WishlistItemResponse(
    Long wishlistId,
    Long itemId,
    String itemName,
    String imageUrl,
    BigDecimal dailyRent,
    String location,
    String status,
    LocalDateTime addedAt
) {
}
