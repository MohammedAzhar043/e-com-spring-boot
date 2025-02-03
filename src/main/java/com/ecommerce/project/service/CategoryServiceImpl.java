package com.ecommerce.project.service;

import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class CategoryServiceImpl implements CategoryService {


    private Long nextId = 1L;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {

        Optional<Category> savedCategoryOptional = categoryRepository.findById(categoryId);
        /*Category category = savedCategoryOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "category  with category id " + categoryId + " not found"));*/

        Category category1 = savedCategoryOptional.orElseThrow(() ->
                new ResourceNotFoundException("Category", "categoryId", categoryId));
       /* categoryRepository.delete(category);*/
        categoryRepository.delete(category1);
        return "category with category id :" + categoryId +" deleted successfully ";


    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {

        Optional<Category> savedCategoryOptional = categoryRepository.findById(categoryId);
   /*     Category savedCategory = savedCategoryOptional.orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not found"));*/
        Category savedCategory1 = savedCategoryOptional.orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        category.setCategoryId(categoryId);
     /*   savedCategory = categoryRepository.save(category);
        return savedCategory;*/
        savedCategory1 = categoryRepository.save(category);
        return savedCategory1;

    }
}
