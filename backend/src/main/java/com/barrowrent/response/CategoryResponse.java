
package com.barrowrent.response;

import java.time.LocalDateTime;

public record CategoryResponse(

    Long id,

    String name,

    String description,

    LocalDateTime createdAt,

    LocalDateTime updatedAt

) {
}
