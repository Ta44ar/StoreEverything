package com.niedzwiecki_syperek.StoreEverything.Services;

import com.niedzwiecki_syperek.StoreEverything.Repositories.CategoryRepository;
import com.niedzwiecki_syperek.StoreEverything.db.entities.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public void save(Category category) {
        categoryRepository.save(category);
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public List<Category> findAllSortedByPopularity() {
        return categoryRepository.findAllSortedByPopularity();
    }
}