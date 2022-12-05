package com.proton.easycooking.models;

import java.io.Serializable;

public class Recipe implements Serializable {
    private String recipeId;
    private String categoryId;
    private String recipeName;
    private String recipeDescription;
    private String recipeImage;
    private String recipeView;
    private String recipeFav;
    private String postId;

    public Recipe() {
    }

    public Recipe(String recipeId, String categoryId, String recipeName, String recipeDescription, String recipeImage, String recipeView, String recipeFav, String postId) {
        this.recipeId = recipeId;
        this.categoryId = categoryId;
        this.recipeName = recipeName;
        this.recipeDescription = recipeDescription;
        this.recipeImage = recipeImage;
        this.recipeView = recipeView;
        this.recipeFav = recipeFav;
        this.postId = postId;
    }


    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeDescription() {
        return recipeDescription;
    }

    public void setRecipeDescription(String recipeDescription) {
        this.recipeDescription = recipeDescription;
    }

    public String getRecipeImage() {
        return recipeImage;
    }

    public void setRecipeImage(String recipeImage) {
        this.recipeImage = recipeImage;
    }

    public String getRecipeView() {
        return recipeView;
    }

    public void setRecipeView(String recipeView) {
        this.recipeView = recipeView;
    }

    public String getRecipeFav() {
        return recipeFav;
    }

    public void setRecipeFav(String recipeFav) {
        this.recipeFav = recipeFav;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
