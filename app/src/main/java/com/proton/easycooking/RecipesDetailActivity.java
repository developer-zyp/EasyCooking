package com.proton.easycooking;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.proton.easycooking.models.Recipes;
import com.proton.easycooking.R;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipesDetailActivity extends AppCompatActivity {

    static final String TAG = "RecipesDetail";

    Context context;

    CollapsingToolbarLayout collapsingToolbarLayout;
    CoordinatorLayout coordinatorLayout;

    ImageView img_fav, recipeImage;
    TextView tv_recipeName, tv_recipeDetail;

    ProgressDialog progressDialog;

    DatabaseHelper db;

    List<Recipes> recipesList = new ArrayList<Recipes>();
    List<Recipes> itemFavorites = new ArrayList<>();
    String recipeDescription = "";
    String[] recipeIM;

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_detail);

        MobileAds.initialize(this);
        if(Config.ENABLE_ADMOB_BANNER_ADS){
            loadAdMobBannerAd();
        }

        context = this;
        db = DatabaseHelper.getInstance(this);

        recipeImage = (ImageView) findViewById(R.id.recipe_image);
        tv_recipeName = (TextView) findViewById(R.id.recipe_Name);

        tv_recipeDetail = (TextView) findViewById(R.id.recipe_detail);

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

        progressDialog = new ProgressDialog(context, R.style.MyProgressDialogStyle);
        progressDialog.setMessage("Loading ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        new NetworkTask().execute(APIClient.BASE_URL);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (NetworkTask.checkServerConnection) {
                    getRecipeDetail();

//                    if (progressDialog != null && progressDialog.isShowing())
//                        progressDialog.dismiss();

                } else {
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Warning!");
                    alertDialog.setMessage("No Internet Connection.");
                    alertDialog.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            recreate();
                        }
                    });
                    alertDialog.show();
                }
            }
        }, 1000);

        img_fav = (FloatingActionButton) findViewById(R.id.img_fav);

        img_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemFavorites = db.getRecipe(recipesList.get(0).getRecipeId());
                if (itemFavorites.size() == 0) {

                    db.addRecipe(recipesList.get(0));
                    Toast.makeText(getApplicationContext(), "Added to Favorite!", Toast.LENGTH_SHORT).show();
                    img_fav.setImageResource(R.drawable.ic_favorite_white);

                    updateFavCount();

                } else {

                    db.deleteRecipe(recipesList.get(0));
                    Toast.makeText(getApplicationContext(), "Removed from Favorite!", Toast.LENGTH_SHORT).show();
                    img_fav.setImageResource(R.drawable.ic_favorite_outline_white);

                }
            }
        });

    }

    public void getRecipeDetail() {
        Call<List<Recipes>> callRecipe = APIClient.getService().create(APIInterface.class).getRecipeById(GlobalClass.recipeId);
        callRecipe.enqueue(new Callback<List<Recipes>>() {
            @Override
            public void onResponse(Call<List<Recipes>> call, Response<List<Recipes>> response) {
                if (response.body() != null) {
                    recipesList.addAll(response.body());
                    bindData();

                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    //Toast.makeText(getApplicationContext(), "Data is received!", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<List<Recipes>> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "ERROR : Fail to receive data!", Toast.LENGTH_LONG).show();
                System.out.println("Fail: " + t.getMessage());

            }
        });
    }

    private void bindData() {
        if (recipesList.size() > 0) {
            Glide.with(this).load(recipesList.get(0).getRecipeImage())
                    .placeholder(R.drawable.simple_img)
                    .into(recipeImage);

            tv_recipeName.setText(recipesList.get(0).getRecipeName());
            recipeDescription = recipesList.get(0).getRecipeDescription() + "\n\n";

            tv_recipeDetail.setText(recipeDescription);

            itemFavorites = db.getRecipe(recipesList.get(0).getRecipeId());
            if (itemFavorites.size() == 0) {
                img_fav.setImageResource(R.drawable.ic_favorite_outline_white);
            } else {
                img_fav.setImageResource(R.drawable.ic_favorite_white);

            }


        }
    }

    private void updateFavCount() {
        Call<ResponseBody> callRecipe = APIClient.getService().create(APIInterface.class)
                .updateRecipeView(recipesList.get(0));
        callRecipe.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                //Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "ERROR : Fail to receive data!", Toast.LENGTH_LONG).show();
                System.out.println("Fail: " + t.getMessage());

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

    private void loadAdMobBannerAd(){
        adView = (AdView)findViewById(R.id.detail_adView);
        CardView adCardView = (CardView)findViewById(R.id.cv_detail_ads);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adCardView.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adCardView.setVisibility(View.VISIBLE);
            }

        });

        adCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "AD Clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}