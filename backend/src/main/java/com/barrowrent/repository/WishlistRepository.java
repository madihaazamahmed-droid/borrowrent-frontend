
package com.barrowrent.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.barrowrent.entity.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // Check whether an item is already in the user's wishlist
    boolean existsByUserIdAndItemId(Long userId, Long itemId);

    // Find a specific wishlist entry belonging to a user
    Optional<Wishlist> findByUserIdAndItemId(Long userId, Long itemId);

    // Get all wishlist items of a user, newest first
    List<Wishlist> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Remove an item from a particular user's wishlist
    void deleteByUserIdAndItemId(Long userId, Long itemId);
}
