
package com.barrowrent.response;

import java.time.LocalDateTime;

public record NotificationResponse(

    Long id,

    Long bookingId,

    String title,

    String message,

    boolean read,

    LocalDateTime createdAt

) {
}
