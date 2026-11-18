package com.barrowrent.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.barrowrent.entity.Category;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> { 
	// Find a category by its name, ignoring uppercase/lowercase differences 
	Optional<Category> findByNameIgnoreCase(String name); 
	// Check whether a category with the given name already exists 
	boolean existsByNameIgnoreCase(String name); 
	}
