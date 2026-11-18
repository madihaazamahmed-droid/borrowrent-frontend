package com.barrowrent.response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.barrowrent.enums.BookingStatus;

public record BookingResponse(
        Long id,
        Long itemId,
        String itemTitle,
        Long borrowerId,
        String borrowerName,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalAmount,
        BookingStatus status,
        String message,
        LocalDateTime requestedAt,
        LocalDateTime approvedAt,
        LocalDateTime returnedAt
) {
}
