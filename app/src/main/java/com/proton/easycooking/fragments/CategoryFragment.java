package com.proton.easycooking.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
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
import com.proton.easycooking.MainActivity;
import com.proton.easycooking.NetworkTask;
import com.proton.easycooking.R;
import com.proton.easycooking.adapters.AdapterCategory;
import com.proton.easycooking.models.Category;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CategoryFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout = null;
    private LinearLayout rootLayout;
    private RelativeLayout no_network_Layout, grid_layout;

    private ArrayList<Category> dataCategory;
    RecyclerView recyclerView;
    AdapterCategory adapterCategory;

    //1 for Recipe Category //2 for CaloriesAmt Category
    int selectFragment;

    public CategoryFragment(int selectFragment) {
        this.selectFragment = selectFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_category, container, false);

        rootLayout = (LinearLayout) fragmentView.findViewById(R.id.rootLayout);

//        if (Config.ENABLE_RTL_MODE) {
//            rootLayout.setRotationY(180);
//        }

        if(selectFragment == 1){
            ((MainActivity) getContext()).setTitle("Category");
        }

        no_network_Layout = (RelativeLayout) fragmentView.findViewById(R.id.no_network);
        grid_layout = (RelativeLayout) fragmentView.findViewById(R.id.grid_layout);

        swipeRefreshLayout = (SwipeRefreshLayout) fragmentView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        dataCategory = new ArrayList<Category>();
        adapterCategory = new AdapterCategory(getActivity(), dataCategory, selectFragment);
        recyclerView.setAdapter(adapterCategory);

        no_network_Layout.setVisibility(View.GONE);
        grid_layout.setVisibility(View.VISIBLE);

        swipeRefreshLayout.setRefreshing(true);
        new NetworkTask().execute(APIClient.BASE_URL);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (NetworkTask.checkServerConnection) {
                    getCategoryList();
                    no_network_Layout.setVisibility(View.GONE);
                    grid_layout.setVisibility(View.VISIBLE);

                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    no_network_Layout.setVisibility(View.VISIBLE);
                    grid_layout.setVisibility(View.GONE);

                }

            }
        }, 1000);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new NetworkTask().execute(APIClient.BASE_URL);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        swipeRefreshLayout.setRefreshing(false);
                        if (NetworkTask.checkServerConnection) {
                            getCategoryList();
                            no_network_Layout.setVisibility(View.GONE);
                            grid_layout.setVisibility(View.VISIBLE);

                        } else {
                            if(dataCategory.size() > 0){
                                Toast.makeText(getContext(), "No Internet Connection!",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else{
                                no_network_Layout.setVisibility(View.VISIBLE);
                                grid_layout.setVisibility(View.GONE);
                            }
                        }

                    }
                }, 1000);
            }
        });

        setHasOptionsMenu(true);

        return fragmentView;
    }

    private void getCategoryList() {
        Call<List<Category>> callCategory;
        if(selectFragment == 1){
            callCategory = APIClient.getService().create(APIInterface.class).getCategory();
        }
        else {
            callCategory = APIClient.getService().create(APIInterface.class).getCaloriesAmt();
        }

        callCategory.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.body() != null) {
                    dataCategory.clear();
                    dataCategory.addAll(response.body());

                    adapterCategory.setCategoryList(response.body());

                    swipeRefreshLayout.setRefreshing(false);
                    //Toast.makeText(getContext(), dataCategory.size() + " data is received!", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "ERROR : Fail to receive data!", Toast.LENGTH_SHORT).show();
                System.out.println("Fail: " + t.getMessage());

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Category> searchCategory = new ArrayList<>();
                for (Category item : dataCategory) {
                    if (item.getCategoryName().contains(newText)) {
                        searchCategory.add(item);
                    }
                }
                adapterCategory.setCategoryList(searchCategory);

                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    searchItem.collapseActionView();
                    //searchView.setQuery("", false);
                    adapterCategory.setCategoryList(dataCategory);

//                    adapterCategory = new AdapterCategory(getActivity(), dataCategory, selectFragment);
//                    recyclerView.setAdapter(adapterCategory);
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });
    }

}