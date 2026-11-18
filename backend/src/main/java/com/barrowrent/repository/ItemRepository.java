
package com.barrowrent.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.barrowrent.entity.Item;
import com.barrowrent.enums.ItemStatus;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // PUBLIC ITEM SEARCH WITH COMBINED FILTERS

    @Query("""
        SELECT i FROM Item i
        WHERE (:status IS NULL OR i.status = :status)

        AND (:categoryId IS NULL OR i.category.id = :categoryId)

        AND (
            :name IS NULL OR
            LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))
        )

        AND (:minRent IS NULL OR i.dailyRent >= :minRent)

        AND (:maxRent IS NULL OR i.dailyRent <= :maxRent)

        """)
    Page<Item> searchPublicItems(

        @Param("status") ItemStatus status,

        @Param("categoryId") Long categoryId,

        @Param("name") String name,

        @Param("minRent") BigDecimal minRent,

        @Param("maxRent") BigDecimal maxRent,

        Pageable pageable
    );

    // GET ITEMS BELONGING TO A PARTICULAR OWNER

    Page<Item> findByOwnerId(
        Long ownerId,
        Pageable pageable
    );

    // FIND ITEM BELONGING TO A PARTICULAR OWNER

    Optional<Item> findByIdAndOwnerId(
        Long itemId,
        Long ownerId
    );
}
