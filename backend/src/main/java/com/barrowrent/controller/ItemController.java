package com.barrowrent.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.barrowrent.enums.ItemStatus;
import com.barrowrent.request.CreateItemRequest;
import com.barrowrent.request.UpdateItemRequest;
import com.barrowrent.response.ItemResponse;
import com.barrowrent.security.CurrentUser;
import com.barrowrent.service.ItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/items")
@Validated
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(
            @Valid @RequestBody CreateItemRequest request) {

        ItemResponse response =
            itemService.createItem(CurrentUser.getId(), request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponse> getItemById(
            @PathVariable Long itemId) {

        return ResponseEntity.ok(
            itemService.getItemById(itemId)
        );
    }

    @GetMapping
    public ResponseEntity<Page<ItemResponse>> getItems(
            @RequestParam(required = false) ItemStatus status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minRent,
            @RequestParam(required = false) BigDecimal maxRent,
            @PageableDefault(size = 10, sort = "createdAt")
            Pageable pageable) {

        return ResponseEntity.ok(
            itemService.getItems(
                status,
                categoryId,
                name,
                minRent,
                maxRent,
                pageable
            )
        );
    }

    @GetMapping("/my-items")
    public ResponseEntity<Page<ItemResponse>> getMyItems(
            @PageableDefault(size = 10, sort = "createdAt")
            Pageable pageable) {

        return ResponseEntity.ok(
            itemService.getMyItems(CurrentUser.getId(), pageable)
        );
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ItemResponse> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateItemRequest request) {

        return ResponseEntity.ok(
            itemService.updateItem(
                itemId,
                CurrentUser.getId(),
                request
            )
        );
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long itemId) {

        itemService.deleteItem(itemId, CurrentUser.getId());

        return ResponseEntity.noContent().build();
    }
}