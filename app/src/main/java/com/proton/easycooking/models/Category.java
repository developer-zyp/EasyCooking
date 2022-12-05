package com.proton.easycooking.models;

import java.io.Serializable;

public class Category implements Serializable {
    private String categoryId;
    private String categoryName;
    private String categoryImage;
    private String type;

    public Category() {
    }


    public Category(String categoryId, String categoryName, String categoryImage, String type) {
        super();
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
        this.type = type;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }


}
