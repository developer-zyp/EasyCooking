package com.proton.easycooking.fragments;

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
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.proton.easycooking.APIClient;
import com.proton.easycooking.APIInterface;
import com.proton.easycooking.AppTools;
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

    //1 for Recipe Category
    //2 for SetItem Category
    int selectFragment;
    RecyclerView recyclerView;
    AdapterCategory adapterCategory;
    private List<Category> dataCategory;

    public CategoryFragment(int selectFragment) {
        this.selectFragment = selectFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_category, container, false);

//        rootLayout = fragmentView.findViewById(R.id.rootLayout);

        int spanCount = 2;
        switch (selectFragment) {
            case 1:
            case 4:
                //((MainActivity) getContext()).setTitle("Category");
                break;
            case 3:
            case 2:
            case 5:
                //((MainActivity) getContext()).setTitle("SetMenu");
                //((MainActivity) getContext()).setTitle("NewPost");
                spanCount = 1;
                break;
        }

        no_network_Layout = fragmentView.findViewById(R.id.no_network);
        grid_layout = fragmentView.findViewById(R.id.grid_layout);

        swipeRefreshLayout = fragmentView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        recyclerView = fragmentView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        dataCategory = new ArrayList<>();
        adapterCategory = new AdapterCategory(getContext(), R.layout.lsv_item_category, selectFragment);
        recyclerView.setAdapter(adapterCategory);

        no_network_Layout.setVisibility(View.GONE);
        grid_layout.setVisibility(View.VISIBLE);

        swipeRefreshLayout.setRefreshing(true);
        new Handler().post(() -> {
            if (AppTools.isNetworkAvailable(getContext())) {
                getCategoryList();
                no_network_Layout.setVisibility(View.GONE);
                grid_layout.setVisibility(View.VISIBLE);

            } else {
                swipeRefreshLayout.setRefreshing(false);
                no_network_Layout.setVisibility(View.VISIBLE);
                grid_layout.setVisibility(View.GONE);

            }

        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Handler().post(() -> {
                if (AppTools.isNetworkAvailable(getContext())) {
                    getCategoryList();
                    no_network_Layout.setVisibility(View.GONE);
                    grid_layout.setVisibility(View.VISIBLE);

                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    if (dataCategory.size() > 0) {
                        Toast.makeText(getContext(), "No Internet Connection!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        no_network_Layout.setVisibility(View.VISIBLE);
                        grid_layout.setVisibility(View.GONE);
                    }
                }
            });
        });

        setHasOptionsMenu(true);

        return fragmentView;
    }

    private void getCategoryList() {
        Call<List<Category>> callCategory;
        switch (selectFragment) {
            case 2:
                callCategory = APIClient.getService().create(APIInterface.class).getNewPost();
                break;
            case 3:
                callCategory = APIClient.getService().create(APIInterface.class).getSetItem();
                break;
            case 1:
                callCategory = APIClient.getService().create(APIInterface.class).getCategory();
                break;
            case 4:
                callCategory = APIClient.getService().create(APIInterface.class).getSnack();
                break;
            default:
                callCategory = APIClient.getService().create(APIInterface.class).getCaloriesAmt();
                break;
        }

        callCategory.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dataCategory = response.body();
                    adapterCategory.setCategoryList(dataCategory);

                    swipeRefreshLayout.setRefreshing(false);
                    //Toast.makeText(getContext(), dataCategory.size() + " data is received!", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "ERROR : Fail to load data!", Toast.LENGTH_SHORT).show();
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

//        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if (!hasFocus) {
//                    searchItem.collapseActionView();
//                    //searchView.setQuery("", false);
//                    adapterCategory.setCategoryList(dataCategory);
//
//                    swipeRefreshLayout.setEnabled(true);
//                } else {
//                    swipeRefreshLayout.setEnabled(false);
//                }
//            }
//        });
    }

}