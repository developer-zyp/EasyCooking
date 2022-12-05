package com.proton.easycooking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdSize;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.proton.easycooking.models.Recipe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipesDetailActivity extends AppCompatActivity {

    static final String TAG = "RecipesDetailActivity";

    Context context;
    DatabaseHelper db;

    CollapsingToolbarLayout collapsingToolbarLayout;
    CoordinatorLayout coordinatorLayout;

    FloatingActionButton img_fav;
    ImageView recipeImage;
    TextView tv_recipeName, tv_recipeDetail;
    WebView webDetail;

    List<Recipe> recipeList;
    List<Recipe> itemFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_detail);

        context = this;
        db = DatabaseHelper.getInstance(this);

        initToolbar();

        recipeImage = (ImageView) findViewById(R.id.recipe_image);
        tv_recipeName = (TextView) findViewById(R.id.recipe_Name);
        tv_recipeDetail = (TextView) findViewById(R.id.recipe_detail);
        img_fav = (FloatingActionButton) findViewById(R.id.img_fav);

        webDetail = findViewById(R.id.web_detail);

        recipeList = new ArrayList<>();
        itemFavorites = new ArrayList<>();
        recipeList.add(AppTools.itemRecipeDetail);
        recipeList.size();
        Glide.with(this).load(recipeList.get(0).getRecipeImage())
                .placeholder(R.drawable.simple_img)
                .into(recipeImage);

        tv_recipeName.setText(recipeList.get(0).getRecipeName());

        String recipeDescription = recipeList.get(0).getRecipeDescription();
        tv_recipeDetail.setText(Html.fromHtml(recipeDescription));

        webDetail.loadData(recipeDescription, "text/html", "utf-8");

        itemFavorites = db.getRecipe(recipeList.get(0).getRecipeId());
        if (itemFavorites.size() == 0) {
            img_fav.setImageResource(R.drawable.ic_favorite_outline_white);
        } else {
            img_fav.setImageResource(R.drawable.ic_favorite_white);

        }

        updateFavCount(true);

        img_fav.setOnClickListener(view -> {
            itemFavorites = db.getRecipe(recipeList.get(0).getRecipeId());
            if (itemFavorites.size() == 0) {

                db.addRecipe(recipeList.get(0));
                Toast.makeText(context, "Added to Favorite!", Toast.LENGTH_SHORT).show();
                img_fav.setImageResource(R.drawable.ic_favorite_white);

                updateFavCount(false);

            } else {

                db.deleteRecipe(recipeList.get(0));
                Toast.makeText(context, "Removed from Favorite!", Toast.LENGTH_SHORT).show();
                img_fav.setImageResource(R.drawable.ic_favorite_outline_white);

            }
        });

        CardView adCardView = findViewById(R.id.cv_ad_detail);
        AdMob.showBannerAds(this, "admob", adCardView, AdSize.BANNER);

        RelativeLayout layoutAdView = findViewById(R.id.layout_adview);
        AdMob.showBannerAds(this, "admob", layoutAdView, AdSize.LARGE_BANNER);


    }

    private void initToolbar() {
        // toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        // add back arrow to toolbar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset < 60) {
                    collapsingToolbarLayout.setTitle(tv_recipeName.getText().toString());
                    tv_recipeName.setVisibility(View.INVISIBLE);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    tv_recipeName.setVisibility(View.VISIBLE);
                    isShow = false;
                }
            }
        });


    }

    private void updateFavCount(boolean view) {
        Call<JsonObject> callRecipe;
        if (view) {
            callRecipe = APIClient.getService().create(APIInterface.class)
                    .updateRecipeView(recipeList.get(0), 1);
        } else {
            callRecipe = APIClient.getService().create(APIInterface.class)
                    .updateRecipeView(recipeList.get(0), 0);
        }

        callRecipe.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                Log.i(TAG, String.valueOf(response.body()));
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Toast.makeText(context, "ERROR : Fail to receive data!", Toast.LENGTH_LONG).show();
                Log.i(TAG, "fail:" + t.getMessage());

            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_share, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_share:

                String formattedString = "Simple";//android.text.Html.fromHtml(str_desc).toString();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "str_title" + "\n" + formattedString + "\n" + getResources().getString(R.string.share_text) + "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

}