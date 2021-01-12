package com.proton.easycooking;

import com.proton.easycooking.models.AppConfig;
import com.proton.easycooking.models.Category;
import com.proton.easycooking.models.NoEatItem;
import com.proton.easycooking.models.Recipes;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("GetAppConfig")
    Call<List<AppConfig>> getAppConfig();

    @GET("GetRecipe")
    Call<List<Recipes>> getRecipe();

    @GET("GetRecipe")
    Call<List<Recipes>> getRecipeBySpecialIds(@Query("specialIds") String ids);

    @GET("GetRecipe")
    Call<List<Recipes>> getRecipeById(@Query("recipeId") int id);

    @POST("UpdateRecipe")
    Call<ResponseBody> updateRecipeView(@Body Recipes recipe);

    @GET("GetRecipe")
    Call<List<Recipes>> getRecipeByCategoryId(@Query("categoryId") int id);

    @GET("GetCategory")
    Call<List<Category>> getCategory();

    @GET("GetCaloriesAmt")
    Call<List<Category>> getCaloriesAmt();

    @GET("GetCaloriesAmt/{id}")
    Call<List<Category>> getCaloriesAmtById(@Path("id") int id);

    @GET("GetNoEat")
    Call<List<NoEatItem>> getNoEat();


}
