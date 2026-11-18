
package com.barrowrent.mapper;

import org.springframework.stereotype.Component;

import com.barrowrent.entity.Category;
import com.barrowrent.response.CategoryResponse;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {

        return new CategoryResponse(

            category.getId(),

            category.getName(),

            category.getDescription(),

            category.getCreatedAt(),

            category.getUpdatedAt()

        );
    }
}
