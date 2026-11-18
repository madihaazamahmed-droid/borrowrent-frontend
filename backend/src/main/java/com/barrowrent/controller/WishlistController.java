package com.barrowrent.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.barrowrent.response.WishlistItemResponse;
import com.barrowrent.security.CurrentUser;
import com.barrowrent.service.WishlistService;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(
            WishlistService wishlistService) {

        this.wishlistService = wishlistService;
    }

    // Add an item to the logged-in user's wishlist

    @PostMapping("/{itemId}")
    public ResponseEntity<WishlistItemResponse> addItem(
            @PathVariable Long itemId) {

        WishlistItemResponse response =
            wishlistService.addItem(
                CurrentUser.getId(),
                itemId
            );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    // Get logged-in user's wishlist

    @GetMapping
    public ResponseEntity<List<WishlistItemResponse>>
            getMyWishlist() {

        return ResponseEntity.ok(
            wishlistService.getMyWishlist(
                CurrentUser.getId()
            )
        );
    }

    // Remove item from wishlist

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long itemId) {

        wishlistService.removeItem(
            CurrentUser.getId(),
            itemId
        );

        return ResponseEntity.noContent().build();
    }
}
