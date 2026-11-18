package com.barrowrent.service;

import java.util.List;

import com.barrowrent.response.WishlistItemResponse;

public interface WishlistService {

    WishlistItemResponse addItem(
        Long userId,
        Long itemId
    );

    List<WishlistItemResponse> getMyWishlist(
        Long userId
    );

    void removeItem(
        Long userId,
        Long itemId
    );
}
