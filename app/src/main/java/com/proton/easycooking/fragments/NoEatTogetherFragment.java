package com.proton.easycooking.fragments;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.proton.easycooking.APIClient;
import com.proton.easycooking.APIInterface;
import com.proton.easycooking.NetworkTask;
import com.proton.easycooking.R;
import com.proton.easycooking.adapters.AdapterNoEat;
import com.proton.easycooking.models.NoEatItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NoEatTogetherFragment extends Fragment {

    //region Variables
    private SwipeRefreshLayout swipeRefreshLayout = null;
    private LinearLayout rootLayout;
    private RelativeLayout no_network_Layout, grid_layout;

    private ArrayList<NoEatItem> dataNoEatItem;
    RecyclerView recyclerView;
    AdapterNoEat adapterNoEat;

    Boolean checkServer = false;
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_no_eat_together, container, false);

        rootLayout = (LinearLayout) fragmentView.findViewById(R.id.rootLayout);
//        if (Config.ENABLE_RTL_MODE) {
//            rootLayout.setRotationY(180);
//        }

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

        dataNoEatItem = new ArrayList<NoEatItem>();
        adapterNoEat = new AdapterNoEat(getActivity(), dataNoEatItem);
        recyclerView.setAdapter(adapterNoEat);

        swipeRefreshLayout.setRefreshing(true);

        new NetworkTask().execute(APIClient.BASE_URL);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (NetworkTask.checkServerConnection) {
                    getNoEatItemList();
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
                            getNoEatItemList();
                            no_network_Layout.setVisibility(View.GONE);
                            grid_layout.setVisibility(View.VISIBLE);
                        } else {
                            if (dataNoEatItem.size() > 0) {
                                Toast.makeText(getContext(), "No Internet Connection!",
                                        Toast.LENGTH_SHORT).show();
                            } else {
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

    private void getNoEatItemList() {
        Call<List<NoEatItem>> apiCall;

        apiCall = APIClient.getService().create(APIInterface.class).getNoEat();

        apiCall.enqueue(new Callback<List<NoEatItem>>() {
            @Override
            public void onResponse(Call<List<NoEatItem>> call, Response<List<NoEatItem>> response) {
                if (response.body() != null) {
                    dataNoEatItem.clear();
                    dataNoEatItem.addAll(response.body());

                    adapterNoEat.setNoEatItemList(response.body());

                    swipeRefreshLayout.setRefreshing(false);
                    //Toast.makeText(getContext(), dataRecipe.size() + " data is received!", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<List<NoEatItem>> call, Throwable t) {
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
                ArrayList<NoEatItem> searchNoEatItems = new ArrayList<>();
                for (NoEatItem item : dataNoEatItem) {
                    if (item.getItems().contains(newText)) {
                        searchNoEatItems.add(item);
                    }
                }
                adapterNoEat.setNoEatItemList(searchNoEatItems);

                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    searchItem.collapseActionView();
                    //searchView.setQuery("", false);
                    adapterNoEat.setNoEatItemList(dataNoEatItem);

                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(getContext(),"This is R.id.Home",Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void closeKeyboard() {

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null) {
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}