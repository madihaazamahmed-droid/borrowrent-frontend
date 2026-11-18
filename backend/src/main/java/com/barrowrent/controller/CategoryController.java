
package com.barrowrent.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.barrowrent.entity.Category;
import com.barrowrent.exception.ResourceNotFoundException;
import com.barrowrent.mapper.CategoryMapper;
import com.barrowrent.repository.CategoryRepository;
import com.barrowrent.request.CategoryRequest;
import com.barrowrent.response.CategoryResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryController(
            CategoryRepository categoryRepository,
            CategoryMapper categoryMapper) {

        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    // CREATE CATEGORY - ADMIN ONLY
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request) {

        String name = request.getName().trim();

        if (categoryRepository.existsByNameIgnoreCase(name)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(request.getDescription());

        Category savedCategory = categoryRepository.save(category);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryMapper.toResponse(savedCategory));
    }

    // GET ALL CATEGORIES
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {

        List<CategoryResponse> categories =
                categoryRepository.findAll()
                    .stream()
                    .map(categoryMapper::toResponse)
                    .toList();

        return ResponseEntity.ok(categories);
    }

    // GET CATEGORY BY ID
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Category not found with ID: " + categoryId
            ));

        return ResponseEntity.ok(categoryMapper.toResponse(category));
    }
}
