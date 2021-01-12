package com.proton.easycooking.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Recipes {
    @SerializedName("recipeId")
    @Expose
    private String recipeId;
    @SerializedName("categoryId")
    @Expose
    private String categoryId;
    @SerializedName("recipeName")
    @Expose
    private String recipeName;
    @SerializedName("recipeDescription")
    @Expose
    private String recipeDescription;
    @SerializedName("recipeImage")
    @Expose
    private String recipeImage;
    @SerializedName("recipeView")
    @Expose
    private String recipeView;

    public Recipes() {
    }

    public Recipes(String recipeId, String categoryId, String recipeName, String recipeDescription, String recipeImage, String recipeView) {
        this.recipeId = recipeId;
        this.categoryId = categoryId;
        this.recipeName = recipeName;
        this.recipeDescription = recipeDescription;
        this.recipeImage = recipeImage;
        this.recipeView = recipeView;
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

}
