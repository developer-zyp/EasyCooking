package com.proton.easycooking.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.proton.easycooking.APIClient;
import com.proton.easycooking.APIInterface;
import com.proton.easycooking.Config;
import com.proton.easycooking.DatabaseHelper;
import com.proton.easycooking.GlobalClass;
import com.proton.easycooking.MainActivity;
import com.proton.easycooking.NetworkTask;
import com.proton.easycooking.R;
import com.proton.easycooking.adapters.AdapterSpecial;
import com.proton.easycooking.models.AppConfig;
import com.proton.easycooking.models.Recipes;

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

    //region Variables
    private SwipeRefreshLayout swipeRefreshLayout = null;
    private LinearLayout rootLayout;
    private RelativeLayout no_network_Layout, grid_layout;

    private ArrayList<Recipes> dataRecipe;

    ArrayList<AppConfig> appConfigList;
    RecyclerView recyclerView;
    AdapterSpecial adapterSpecial;

    APIInterface apiInterface;
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_today_special, container, false);

        rootLayout = (LinearLayout) fragmentView.findViewById(R.id.rootLayout);
//        if (Config.ENABLE_RTL_MODE) {
//            rootLayout.setRotationY(180);
//        }

        apiInterface = APIClient.getService().create(APIInterface.class);
        if (GlobalClass.categoryId > 0) {
            ((MainActivity) getContext()).setTitle("Recipes");

        } else {
            ((MainActivity) getContext()).setTitle("Today Special");

        }

        no_network_Layout = (RelativeLayout) fragmentView.findViewById(R.id.no_network);
        grid_layout = (RelativeLayout) fragmentView.findViewById(R.id.grid_layout);

        //SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) fragmentView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        //RecyclerView
        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        appConfigList = new ArrayList<>();
        //dataRecipe.clear();
        dataRecipe = new ArrayList<Recipes>();
        adapterSpecial = new AdapterSpecial(getActivity(), dataRecipe);
        recyclerView.setAdapter(adapterSpecial);

        swipeRefreshLayout.setRefreshing(true);

        new NetworkTask().execute(APIClient.BASE_URL);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (NetworkTask.checkServerConnection) {
                    getRecipeList(false);
                    no_network_Layout.setVisibility(View.GONE);
                    grid_layout.setVisibility(View.VISIBLE);

                } else {
                    grid_layout.setVisibility(View.GONE);
                    no_network_Layout.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }, 1500);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    new NetworkTask().execute(APIClient.BASE_URL);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            swipeRefreshLayout.setRefreshing(false);
                            if (NetworkTask.checkServerConnection) {
                                getRecipeList(false);
                                no_network_Layout.setVisibility(View.GONE);
                                grid_layout.setVisibility(View.VISIBLE);
                            } else {
                                if (dataRecipe.size() > 0) {
                                    Toast.makeText(getContext(), "No Internet Connection!",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    grid_layout.setVisibility(View.GONE);
                                    no_network_Layout.setVisibility(View.VISIBLE);
                                }

                            }


                        }
                    }, 1000);
                } catch (Exception ex) {

                }

            }
        });

        setHasOptionsMenu(true);

        return fragmentView;
    }

    private void getRecipeList(Boolean isRefresh) {
        Call<List<Recipes>> callRecipe;
        if (GlobalClass.categoryId > 0) {
            callRecipe = apiInterface.getRecipeByCategoryId(GlobalClass.categoryId);
        } else {
            callRecipe = apiInterface.getRecipeBySpecialIds(getRandom(isRefresh));
        }
        callRecipe.enqueue(new Callback<List<Recipes>>() {
            @Override
            public void onResponse(Call<List<Recipes>> call, Response<List<Recipes>> response) {
                if (response.body() != null) {
                    dataRecipe.clear();
                    dataRecipe.addAll(response.body());

                    adapterSpecial.setRecipeList(response.body());

                    swipeRefreshLayout.setRefreshing(false);
                    //Toast.makeText(getContext(), dataRecipe.size() + " data is received!", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<List<Recipes>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "ERROR : Fail to receive data!", Toast.LENGTH_SHORT).show();
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
            for (int i = 0; i < Config.NUM_OF_SPECIAL_RECIPES; i++) {

                random = new Random().nextInt(Config.NUM_OF_MAX_RECIPES) + 1; // [0, 60] + 20 => [20, 80]
                while (result.toString().contains(Integer.toString(random))) {
                    random = new Random().nextInt(Config.NUM_OF_MAX_RECIPES) + 1;
                }

                result.append(",").append(random);
            }

            AppConfig newRecipesId = new AppConfig("100", nowDate, result.toString());
            db.addAppConfig(newRecipesId);
        } else {
            if (recipes_date.equals(nowDate) && !isRefresh) {
                result.append(db.getAppConfig(nowDate));
            } else {
                for (int i = 0; i < Config.NUM_OF_SPECIAL_RECIPES; i++) {

                    random = new Random().nextInt(Config.NUM_OF_MAX_RECIPES) + 1; // [0, 60] + 20 => [20, 80]
                    while (result.toString().contains(Integer.toString(random))) {
                        random = new Random().nextInt(Config.NUM_OF_MAX_RECIPES) + 1;
                    }

                    result.append(",").append(random);
                }

                db.updateNewRecipesId(nowDate, result.toString());
            }
        }

        //Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
        return "(0" + result + ")";
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

    private void refreshRecipes(){

        new NetworkTask().execute(APIClient.BASE_URL);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                swipeRefreshLayout.setRefreshing(false);
                if (NetworkTask.checkServerConnection) {
                    getRecipeList(true);
                    no_network_Layout.setVisibility(View.GONE);
                    grid_layout.setVisibility(View.VISIBLE);
                } else {
                    if (dataRecipe.size() > 0) {
                        Toast.makeText(getContext(), "No Internet Connection!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        grid_layout.setVisibility(View.GONE);
                        no_network_Layout.setVisibility(View.VISIBLE);
                    }

                }


            }
        }, 1000);
    }
}
