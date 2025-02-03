package com.ecommerce.project.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long categoryId;
    private String categoryName;

    public Category() {

    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Category(String categoryName, Long categoryId) {
        this.categoryName = categoryName;
        this.categoryId = categoryId;
    }
}
