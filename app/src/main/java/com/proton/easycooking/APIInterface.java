package com.proton.easycooking;

import com.google.gson.JsonObject;
import com.proton.easycooking.models.AppSetting;
import com.proton.easycooking.models.Category;
import com.proton.easycooking.models.NoEatItem;
import com.proton.easycooking.models.Recipe;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIInterface {

    @GET
    Call<List<AppSetting>> getJson(@Url String url);

    @GET("easycooking/GetAppConfig")
    Call<List<AppSetting>> getAppConfig();

    @GET("easycooking/GetRecipe")
    Call<List<Recipe>> getRecipe();

    @GET("easycooking/GetRecipe")
    Call<List<Recipe>> getRecipeBySpecialIds(@Query("specialIds") String ids);

    @GET("easycooking/GetRecipe")
    Call<List<Recipe>> getRecipeById(@Query("recipeId") int id);

    @POST("easycooking/UpdateRecipe")
    Call<JsonObject> updateRecipeView(@Body Recipe recipe, @Query("view") int view);

    @GET("easycooking/GetRecipe")
    Call<List<Recipe>> getRecipeByCategoryId(@Query("categoryId") int id);

    @GET("easycooking/GetRecipe")
    Call<List<Recipe>> getRecipeByPostId(@Query("postid") int id);

    @GET("easycooking/GetCategory")
    Call<List<Category>> getCategory();

    @GET("easycooking/GetCaloriesAmt")
    Call<List<Category>> getCaloriesAmt();

    @GET("easycooking/GetNewPost")
    Call<List<Category>> getNewPost();

    @GET("easycooking/GetSnack")
    Call<List<Category>> getSnack();

    @GET("easycooking/GetSetItem")
    Call<List<Category>> getSetItem();

    @GET("easycooking/GetNoEat")
    Call<List<NoEatItem>> getNoEat();

    //Admin
    @POST("easycooking/UploadCategory")
    Call<JsonObject> uploadCategory(@Body Category category);

    @POST("easycooking/UploadPost")
    Call<JsonObject> uploadPost(@Body Category category);

    @POST("easycooking/UploadRecipe")
    Call<JsonObject> uploadRecipe(@Body Recipe recipe);

    @Multipart()
    @POST("1/upload")
    Call<JsonObject> uploadImage(@Query("expiration") String expiration, @Query("key") String key,
                                 @Part() MultipartBody.Part file);

}
