package com.ecommerce.project.service;

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

//    private List<Category> categories = new ArrayList<>();
    private Long nextId = 1L;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
//        category.setCategoryId(nextId++);
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {

        List<Category> categories =categoryRepository.findAll();

        Category category = categories.stream().
                filter(c -> c.getCategoryId().equals(categoryId)).
                findFirst()
                .orElseThrow(()->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource not found"));

//        categories.remove(category);
        categoryRepository.delete(category);
        return "category with category id :" + categoryId +" deleted successfully ";
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {


        List<Category> categories =categoryRepository.findAll();

        Optional<Category> optionalCategory = categories.stream().
                filter(c -> c.getCategoryId().equals(categoryId)).
                findFirst();
        if(optionalCategory.isPresent()) {
            Category existinigcategory = optionalCategory.get();
            existinigcategory.setCategoryName(category.getCategoryName());
            existinigcategory.setCategoryId(categoryId);
            Category savedCategory = categoryRepository.save(existinigcategory);
            return savedCategory;
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"categoryId "+ categoryId +" not found");
        }

    }
}
