package com.barrowrent.mapper;

import org.springframework.stereotype.Component;

import com.barrowrent.entity.Item;
import com.barrowrent.response.ItemResponse;

@Component
public class ItemMapper {

    public ItemResponse toResponse(Item item) {

        return new ItemResponse(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getDailyRent(),
            item.getCondition(),
            item.getStatus(),
            item.getImageUrl(),
            item.getLocation(),
            item.getOwner().getId(),
            item.getCategory().getId(),
            item.getCategory().getName(),
            item.getCreatedAt(),
            item.getUpdatedAt()
        );
    }
}