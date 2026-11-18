package com.barrowrent.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barrowrent.entity.Item;
import com.barrowrent.entity.User;
import com.barrowrent.entity.Wishlist;
import com.barrowrent.exception.BookingException;
import com.barrowrent.repository.ItemRepository;
import com.barrowrent.repository.UserRepository;
import com.barrowrent.repository.WishlistRepository;
import com.barrowrent.response.WishlistItemResponse;
import com.barrowrent.service.WishlistService;

@Service
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public WishlistServiceImpl(
            WishlistRepository wishlistRepository,
            UserRepository userRepository,
            ItemRepository itemRepository) {

        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public WishlistItemResponse addItem(
            Long userId,
            Long itemId) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BookingException(
                HttpStatus.NOT_FOUND,
                "User not found."
            ));

        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new BookingException(
                HttpStatus.NOT_FOUND,
                "Item not found."
            ));

        if (wishlistRepository.existsByUserIdAndItemId(
                userId, itemId)) {

            throw new BookingException(
                HttpStatus.CONFLICT,
                "Item is already in your wishlist."
            );
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setItem(item);

        return toResponse(wishlistRepository.save(wishlist));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistItemResponse> getMyWishlist(
            Long userId) {

        return wishlistRepository
            .findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public void removeItem(
            Long userId,
            Long itemId) {

        Wishlist wishlist = wishlistRepository
            .findByUserIdAndItemId(userId, itemId)
            .orElseThrow(() -> new BookingException(
                HttpStatus.NOT_FOUND,
                "Item was not found in your wishlist."
            ));

        wishlistRepository.delete(wishlist);
    }

    private WishlistItemResponse toResponse(
            Wishlist wishlist) {

        Item item = wishlist.getItem();

        return new WishlistItemResponse(
            wishlist.getId(),
            item.getId(),
            item.getName(),
            item.getImageUrl(),
            item.getDailyRent(),
            item.getLocation(),
            item.getStatus().name(),
            wishlist.getCreatedAt()
        );
    }
}
