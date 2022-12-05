package com.proton.easycooking.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdSize;
import com.proton.easycooking.APIClient;
import com.proton.easycooking.APIInterface;
import com.proton.easycooking.AdMob;
import com.proton.easycooking.AppTools;
import com.proton.easycooking.MainActivity;
import com.proton.easycooking.R;
import com.proton.easycooking.adapters.AdapterCategory;
import com.proton.easycooking.adapters.RecipeAdapter;
import com.proton.easycooking.adapters.SliderAdapter;
import com.proton.easycooking.models.Category;
import com.proton.easycooking.models.Recipe;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    TextView moreSpecial, moreNewPost, moreSetItem, moreCategory, moreSnack, moreKnowledge;
    RecyclerView recyclerNewPost, recyclerSetItem, recyclerCategory, recyclerSnack, recyclerKnowledge;
    RecipeAdapter specialAdapter, knowledgeAdapter;
    AdapterCategory newPostAdapter, categoryAdapter, setItemAdapter, snackAdapter;
    SliderAdapter sliderAdapter;
    APIInterface apiInterface;
    private List<Recipe> specialList, knowledgeList;
    private List<Category> newPostList, setItemList, categoryList, snackList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        apiInterface = APIClient.getService().create(APIInterface.class);

        initUI(rootView);

        getSpecialRecipes(false);
        getNewPost();
        getSetItem();
        getCategory();
        getSnack();
        getKnowledge();

        RelativeLayout layoutAdView = rootView.findViewById(R.id.layout_adview);
        AdMob.showBannerAds(getContext(), "admob", layoutAdView, AdSize.LARGE_BANNER);

        return rootView;

    }


    private void initUI(View rootView) {
        //ImageSliderView
        specialList = new ArrayList<>();
        SliderView sliderView = rootView.findViewById(R.id.imageSlider);
        sliderAdapter = new SliderAdapter(getContext());
        sliderView.setSliderAdapter(sliderAdapter);
        //sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.startAutoCycle();

        moreSpecial = rootView.findViewById(R.id.see_all_special);
        moreSpecial.setOnClickListener(v -> {
            TodaySpecialFragment todaySpecialFragment = new TodaySpecialFragment();
            MainActivity.fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, todaySpecialFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit();
            //getActivity().setTitle(getResources().getString(R.string.nav_Special));
        });

        //NewPost   #2
        recyclerNewPost = rootView.findViewById(R.id.recycler_new_home);
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerNewPost.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerNewPost.setItemAnimator(new DefaultItemAnimator());

        newPostList = new ArrayList<>();
        newPostAdapter = new AdapterCategory(getContext(), R.layout.lsv_item_home_category, 2);
        recyclerNewPost.setAdapter(newPostAdapter);

        moreNewPost = rootView.findViewById(R.id.see_all_new);
        moreNewPost.setOnClickListener(v -> {
            CategoryFragment categoryFragment = new CategoryFragment(2);
            MainActivity.fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, categoryFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit();
        });

        //SetItem   #3
        recyclerSetItem = rootView.findViewById(R.id.recycler_set_home);
        recyclerSetItem.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerSetItem.setItemAnimator(new DefaultItemAnimator());

        setItemList = new ArrayList<>();
        setItemAdapter = new AdapterCategory(getContext(), R.layout.lsv_item_home_category, 3);
        recyclerSetItem.setAdapter(setItemAdapter);

        moreSetItem = rootView.findViewById(R.id.see_all_set);
        moreSetItem.setOnClickListener(v -> {
            CategoryFragment categoryFragment = new CategoryFragment(3);
            MainActivity.fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, categoryFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit();
        });

        //Category  #1
        recyclerCategory = rootView.findViewById(R.id.recycler_category_home);
        recyclerCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerCategory.setItemAnimator(new DefaultItemAnimator());

        categoryList = new ArrayList<>();
        categoryAdapter = new AdapterCategory(getContext(), R.layout.lsv_item_home_category, 1);
        recyclerCategory.setAdapter(categoryAdapter);

        moreCategory = rootView.findViewById(R.id.see_all_category);
        moreCategory.setOnClickListener(v -> {
            CategoryFragment categoryFragment = new CategoryFragment(1);
            MainActivity.fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, categoryFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit();
        });

        //Snack #4
        recyclerSnack = rootView.findViewById(R.id.recycler_snack_home);
        recyclerSnack.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerSnack.setItemAnimator(new DefaultItemAnimator());

        snackList = new ArrayList<>();
        snackAdapter = new AdapterCategory(getContext(), R.layout.lsv_item_home_category, 4);
        recyclerSnack.setAdapter(snackAdapter);

        moreSnack = rootView.findViewById(R.id.see_all_snack);
        moreSnack.setOnClickListener(v -> {
            CategoryFragment categoryFragment = new CategoryFragment(4);
            MainActivity.fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, categoryFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit();
        });

        //Knowledge
        recyclerKnowledge = rootView.findViewById(R.id.recycler_knowledge_home);
        recyclerKnowledge.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerKnowledge.setItemAnimator(new DefaultItemAnimator());

        knowledgeList = new ArrayList<>();
        knowledgeAdapter = new RecipeAdapter(getContext());
        recyclerKnowledge.setAdapter(knowledgeAdapter);

        moreKnowledge = rootView.findViewById(R.id.see_all_knowledge);
        moreKnowledge.setOnClickListener(v -> {
            TodaySpecialFragment todaySpecialFragment = new TodaySpecialFragment(1000, 0);
            MainActivity.fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, todaySpecialFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit();
        });

    }

    private void getSpecialRecipes(Boolean isRefresh) {
        Call<List<Recipe>> callRecipe = apiInterface.getRecipeBySpecialIds(AppTools.getRandom(getContext(), isRefresh));
        callRecipe.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    specialList = response.body();
                    sliderAdapter.setRecipeList(specialList);
//                    specialAdapter.setRecipeList(specialList);
//                    AppTools.showToast(getContext(), "Count : " + specialList.size());

                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                AppTools.showToast(getContext(), "Fail to load TodaySpecial!");
                Log.e(TAG, t.getMessage());

            }

        });

    }

    private void getNewPost() {
        Call<List<Category>> getRecipe = apiInterface.getNewPost();
        getRecipe.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    newPostList = response.body();
                    newPostAdapter.setCategoryList(AppTools.limitList(newPostList, 10));

                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                AppTools.showToast(getContext(), "Fail to load NewPost!");
                Log.e(TAG, t.getMessage());
            }
        });

    }

    private void getCategory() {
        Call<List<Category>> callCategory = apiInterface.getCategory();
        callCategory.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    categoryAdapter.setCategoryList(AppTools.limitList(categoryList, 10));

                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                AppTools.showToast(getContext(), "Fail to load Category!");
                Log.e(TAG, t.getMessage());

            }
        });
    }

    private void getSnack() {
        Call<List<Category>> callCategory = apiInterface.getSnack();
        callCategory.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    snackList = response.body();
                    snackAdapter.setCategoryList(AppTools.limitList(snackList, 10));

                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                AppTools.showToast(getContext(), "Fail to load Snack!");
                Log.e(TAG, t.getMessage());

            }
        });
    }

    private void getSetItem() {
        Call<List<Category>> callCategory = apiInterface.getSetItem();
        callCategory.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setItemList = response.body();
                    setItemAdapter.setCategoryList(AppTools.limitList(setItemList, 10));

                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                AppTools.showToast(getContext(), "Fail to load SetItem!");
                Log.e(TAG, t.getMessage());

            }
        });
    }

    private void getKnowledge() {
        Call<List<Recipe>> callRecipe = apiInterface.getRecipeByCategoryId(1000);
        callRecipe.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    knowledgeList = response.body();
                    knowledgeAdapter.setRecipeList(AppTools.limitList(knowledgeList, 10));

                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                AppTools.showToast(getContext(), "Fail to load Knowledge!");
                Log.e(TAG, t.getMessage());

            }

        });

    }

}