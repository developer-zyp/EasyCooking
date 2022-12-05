package com.proton.easycooking.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.proton.easycooking.APIClient;
import com.proton.easycooking.APIInterface;
import com.proton.easycooking.AppConfig;
import com.proton.easycooking.AppTools;
import com.proton.easycooking.DatabaseHelper;
import com.proton.easycooking.R;
import com.proton.easycooking.adapters.AdapterSpecial;
import com.proton.easycooking.models.AppSetting;
import com.proton.easycooking.models.Recipe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TodaySpecialFragment extends Fragment {

    ArrayList<AppSetting> appSettingList;
    private int categoryId = 0;
    private int postId = 0;
    private List<Recipe> dataRecipe;

    //region Variables
    private SwipeRefreshLayout swipeRefreshLayout = null;
    private LinearLayout rootLayout;
    private RelativeLayout no_network_Layout, grid_layout;

    public TodaySpecialFragment() {
        // Required empty public constructor
    }

    public TodaySpecialFragment(int categoryId, int postId) {
        this.categoryId = categoryId;
        this.postId = postId;
    }

    RecyclerView recyclerView;
    AdapterSpecial adapterSpecial;

    APIInterface apiInterface;
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_today_special, container, false);

//        rootLayout = fragmentView.findViewById(R.id.rootLayout);

//        if (Config.ENABLE_RTL_MODE) {
//            rootLayout.setRotationY(180);
//        }

        apiInterface = APIClient.getService().create(APIInterface.class);
        if (categoryId > 0 || postId > 0) {
            //((MainActivity) getContext()).setTitle("Recipes");
            setHasOptionsMenu(false);

        } else {
            //((MainActivity) getContext()).setTitle("Today Special");
            setHasOptionsMenu(true);

        }

        no_network_Layout = fragmentView.findViewById(R.id.no_network);
        grid_layout = fragmentView.findViewById(R.id.grid_layout);

        //SwipeRefreshLayout
        swipeRefreshLayout = fragmentView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        //RecyclerView
        recyclerView = fragmentView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        appSettingList = new ArrayList<>();
        dataRecipe = new ArrayList<>();
        adapterSpecial = new AdapterSpecial(getActivity(), dataRecipe);
        recyclerView.setAdapter(adapterSpecial);

        swipeRefreshLayout.setRefreshing(true);

        new Handler().post(() -> {
            if (AppTools.isNetworkAvailable(getContext())) {
                getRecipeList(false);
                no_network_Layout.setVisibility(View.GONE);
                grid_layout.setVisibility(View.VISIBLE);

            } else {
                grid_layout.setVisibility(View.GONE);
                no_network_Layout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    new Handler().postDelayed(() -> {

                        if (AppTools.isNetworkAvailable(getContext())) {
                            getRecipeList(false);
                            no_network_Layout.setVisibility(View.GONE);
                            grid_layout.setVisibility(View.VISIBLE);
                        } else {

                            swipeRefreshLayout.setRefreshing(false);
                            if (dataRecipe.size() > 0) {
                                Toast.makeText(getContext(), "No Internet Connection!",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                grid_layout.setVisibility(View.GONE);
                                no_network_Layout.setVisibility(View.VISIBLE);
                            }

                        }

                    }, 1000);
                } catch (Exception ex) {

                }

            }
        });

        //setHasOptionsMenu(true);

        return fragmentView;
    }

    private void getRecipeList(Boolean isRefresh) {
        Call<List<Recipe>> callRecipe;
        if (categoryId > 0) {
            callRecipe = apiInterface.getRecipeByCategoryId(categoryId);
        } else if (postId > 0) {
            callRecipe = apiInterface.getRecipeByPostId(postId);
        } else {
            callRecipe = apiInterface.getRecipeBySpecialIds(getRandom(isRefresh));
        }
        callRecipe.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.body() != null) {
                    dataRecipe = response.body();
                    adapterSpecial.setRecipeList(dataRecipe);
                    swipeRefreshLayout.setRefreshing(false);
                    //Toast.makeText(getContext(), dataRecipe.size() + " data is received!", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "ERROR : Fail to load data!", Toast.LENGTH_SHORT).show();
                System.out.println("Fail: " + t.getMessage());

            }

        });

    }

    private String getRandom(Boolean isRefresh) {
        StringBuilder result = new StringBuilder("");
        int random = 0;

        String nowDate = getDateTime();
        DatabaseHelper db = DatabaseHelper.getInstance(getActivity());
        String recipes_date = db.getNewRecipesId("100");

        if (recipes_date == null) {
            for (int i = 0; i < AppConfig.NUM_OF_SPECIAL_RECIPES; i++) {

                random = new Random().nextInt(AppConfig.NUM_OF_MAX_RECIPES) + 1; // [0, 60] + 20 => [20, 80]
                while (result.toString().contains(Integer.toString(random))) {
                    random = new Random().nextInt(AppConfig.NUM_OF_MAX_RECIPES) + 1;
                }

                result.append(",").append(random);
            }

            AppSetting newRecipesId = new AppSetting("100", nowDate, result.toString());
            db.addAppConfig(newRecipesId);
        } else {
            if (recipes_date.equals(nowDate) && !isRefresh) {
                result.append(db.getAppConfig(nowDate));
            } else {
                for (int i = 0; i < AppConfig.NUM_OF_SPECIAL_RECIPES; i++) {

                    random = new Random().nextInt(AppConfig.NUM_OF_MAX_RECIPES) + 1; // [0, 60] + 20 => [20, 80]
                    while (result.toString().contains(Integer.toString(random))) {
                        random = new Random().nextInt(AppConfig.NUM_OF_MAX_RECIPES) + 1;
                    }

                    result.append(",").append(random);
                }

                db.updateNewRecipesId(nowDate, result.toString());
            }
        }

        //Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
        return "0" + result;
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());// yyyy/MM/dd HH:mm:ss
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_refresh, menu);
        MenuItem refreshItem = menu.findItem(R.id.menu_refresh);
        refreshItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setIcon(R.drawable.ic_launcher);
                alertDialog.setTitle(R.string.app_name);
                alertDialog.setMessage("Are you sure want to refresh?");
                alertDialog.setCancelable(true);
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        swipeRefreshLayout.setRefreshing(true);
                        refreshRecipes();
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();
                return false;
            }
        });

    }

    private void refreshRecipes() {

        new Handler().postDelayed(() -> {
            if (AppTools.isNetworkAvailable(getContext())) {
                getRecipeList(true);
                no_network_Layout.setVisibility(View.GONE);
                grid_layout.setVisibility(View.VISIBLE);
            } else {
                swipeRefreshLayout.setRefreshing(false);
                if (dataRecipe.size() > 0) {
                    Toast.makeText(getContext(), "No Internet Connection!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    grid_layout.setVisibility(View.GONE);
                    no_network_Layout.setVisibility(View.VISIBLE);
                }

            }


        }, 0);
    }
}
