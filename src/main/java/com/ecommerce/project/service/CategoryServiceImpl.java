package com.ecommerce.project.service;

import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {

        List<Category> AllCategories = categoryRepository.findAll();
        if (AllCategories.isEmpty()) {
            throw new APIException("No Category found, please try to insert the new category");
        }
        return AllCategories;
    }

    @Override
    public void createCategory(Category category) {

        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if (savedCategory != null) {
            throw new APIException("Category  with the name " + category.getCategoryName()+" already exists");
        }
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {

        Optional<Category> savedCategoryOptional = categoryRepository.findById(categoryId);

        Category category = savedCategoryOptional.orElseThrow(() ->
                new ResourceNotFoundException("Category", "categoryId", categoryId));
        categoryRepository.delete(category);
        return "category with category id :" + categoryId +" deleted successfully ";


    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {

        Optional<Category> savedCategoryOptional = categoryRepository.findById(categoryId);
        Category savedCategory = savedCategoryOptional.
                orElseThrow(() ->
                        new ResourceNotFoundException("Category", "categoryId", categoryId));

        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);
        return savedCategory;

    }
}
