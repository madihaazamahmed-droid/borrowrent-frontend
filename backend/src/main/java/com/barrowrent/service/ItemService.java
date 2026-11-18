package com.barrowrent.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.barrowrent.enums.ItemStatus;
import com.barrowrent.request.CreateItemRequest;
import com.barrowrent.request.UpdateItemRequest;
import com.barrowrent.response.ItemResponse;

public interface ItemService {

    ItemResponse createItem(
        Long ownerId,
        CreateItemRequest request
    );

    ItemResponse getItemById(Long itemId);

    Page<ItemResponse> getItems(
        ItemStatus status,
        Long categoryId,
        String name,
        BigDecimal minRent,
        BigDecimal maxRent,
        Pageable pageable
    );

    Page<ItemResponse> getMyItems(
        Long ownerId,
        Pageable pageable
    );

    ItemResponse updateItem(
        Long itemId,
        Long ownerId,
        UpdateItemRequest request
    );

    void deleteItem(Long itemId, Long ownerId);
}