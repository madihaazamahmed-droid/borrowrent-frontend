
package com.barrowrent.service.impl;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barrowrent.entity.Category;
import com.barrowrent.entity.Item;
import com.barrowrent.entity.User;
import com.barrowrent.enums.ItemStatus;
import com.barrowrent.exception.ResourceNotFoundException;
import com.barrowrent.mapper.ItemMapper;
import com.barrowrent.repository.CategoryRepository;
import com.barrowrent.repository.ItemRepository;
import com.barrowrent.repository.UserRepository;
import com.barrowrent.request.CreateItemRequest;
import com.barrowrent.request.UpdateItemRequest;
import com.barrowrent.response.ItemResponse;
import com.barrowrent.service.ItemService;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public ItemServiceImpl(
            ItemRepository itemRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            ItemMapper itemMapper) {

        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
    }

    // CREATE ITEM

    @Override
    public ItemResponse createItem(
            Long ownerId,
            CreateItemRequest request) {

        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found with ID: " + ownerId
            ));

        Category category = categoryRepository
            .findById(request.categoryId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Category not found with ID: " + request.categoryId()
            ));

        Item item = new Item();

        item.setName(request.name().trim());
        item.setDescription(request.description().trim());
        item.setDailyRent(request.dailyRent());
        item.setCondition(request.condition());
        item.setImageUrl(request.imageUrl());
        item.setLocation(request.location().trim());

        item.setStatus(ItemStatus.AVAILABLE);

        item.setOwner(owner);
        item.setCategory(category);

        return itemMapper.toResponse(
            itemRepository.save(item)
        );
    }

    // GET ITEM BY ID
    // Public browsing

    @Override
    @Transactional(readOnly = true)
    public ItemResponse getItemById(Long itemId) {

        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Item not found with ID: " + itemId
            ));

        return itemMapper.toResponse(item);
    }

    // GET ITEMS WITH SEARCH AND FILTERS
    // Public browsing

    @Override
    @Transactional(readOnly = true)
    public Page<ItemResponse> getItems(
            ItemStatus status,
            Long categoryId,
            String name,
            BigDecimal minRent,
            BigDecimal maxRent,
            Pageable pageable) {

        if (minRent != null && minRent.signum() < 0) {
            throw new IllegalArgumentException(
                "Minimum rent cannot be negative"
            );
        }

        if (maxRent != null && maxRent.signum() < 0) {
            throw new IllegalArgumentException(
                "Maximum rent cannot be negative"
            );
        }

        if (minRent != null && maxRent != null
                && minRent.compareTo(maxRent) > 0) {

            throw new IllegalArgumentException(
                "Minimum rent cannot exceed maximum rent"
            );
        }

        // Show only AVAILABLE items by default.

        ItemStatus effectiveStatus =
            (status == null)
                ? ItemStatus.AVAILABLE
                : status;

        String effectiveName =
            (name == null || name.isBlank())
                ? null
                : name.trim();

        Page<Item> items =
            itemRepository.searchPublicItems(
                effectiveStatus,
                categoryId,
                effectiveName,
                minRent,
                maxRent,
                pageable
            );

        return items.map(itemMapper::toResponse);
    }

    // GET LOGGED-IN OWNER'S ITEMS

    @Override
    @Transactional(readOnly = true)
    public Page<ItemResponse> getMyItems(
            Long ownerId,
            Pageable pageable) {

        return itemRepository.findByOwnerId(ownerId, pageable)
            .map(itemMapper::toResponse);
    }

    // UPDATE ITEM

    @Override
    public ItemResponse updateItem(
            Long itemId,
            Long ownerId,
            UpdateItemRequest request) {

        Item item = getOwnedItem(itemId, ownerId);

        Category category = categoryRepository
            .findById(request.categoryId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Category not found with ID: " + request.categoryId()
            ));

        item.setName(request.name().trim());
        item.setDescription(request.description().trim());
        item.setDailyRent(request.dailyRent());
        item.setCondition(request.condition());
        item.setImageUrl(request.imageUrl());
        item.setLocation(request.location().trim());
        item.setStatus(request.status());
        item.setCategory(category);

        return itemMapper.toResponse(
            itemRepository.save(item)
        );
    }

    // DELETE ITEM

    @Override
    public void deleteItem(Long itemId, Long ownerId) {

        Item item = getOwnedItem(itemId, ownerId);

        itemRepository.delete(item);
    }

    // VERIFY ITEM OWNERSHIP

    private Item getOwnedItem(
            Long itemId,
            Long currentUserId) {

        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Item not found with ID: " + itemId
            ));

        if (item.getOwner() == null
                || currentUserId == null
                || !currentUserId.equals(
                    item.getOwner().getId()
                )) {

            throw new AccessDeniedException(
                "You are not allowed to modify this item"
            );
        }

        return item;
    }
}
