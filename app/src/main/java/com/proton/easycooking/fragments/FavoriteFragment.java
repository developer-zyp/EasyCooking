package com.proton.easycooking.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.proton.easycooking.APIClient;
import com.proton.easycooking.DatabaseHelper;
import com.proton.easycooking.NetworkTask;
import com.proton.easycooking.R;
import com.proton.easycooking.adapters.AdapterSpecial;
import com.proton.easycooking.models.Recipes;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment {

    //region Variables
    private SwipeRefreshLayout swipeRefreshLayout = null;
    private RelativeLayout no_network_Layout, grid_layout;
    private LinearLayout no_item_Layout;
    private ArrayList<Recipes> dataRecipe;
    RecyclerView recyclerView;
    AdapterSpecial adapterSpecial;

    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_favorite, container, false);

        grid_layout = (RelativeLayout) fragmentView.findViewById(R.id.grid_layout);
        no_network_Layout = (RelativeLayout) fragmentView.findViewById(R.id.no_network);
        no_item_Layout = (LinearLayout) fragmentView.findViewById(R.id.no_item);

        //SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) fragmentView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        //RecyclerView
        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        dataRecipe = new ArrayList<Recipes>();
        adapterSpecial = new AdapterSpecial(getActivity(), dataRecipe);
        recyclerView.setAdapter(adapterSpecial);

        swipeRefreshLayout.setRefreshing(true);
        BindData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BindData();
            }
        });

        return fragmentView;
    }

    private void BindData() {

        new NetworkTask().execute(APIClient.BASE_URL);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                swipeRefreshLayout.setRefreshing(false);
                if (NetworkTask.checkServerConnection) {
                    getFavoriteList();
                    if (dataRecipe.size() > 0) {
                        no_network_Layout.setVisibility(View.GONE);
                        no_item_Layout.setGravity(View.GONE);
                        grid_layout.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getContext(), "No Favorites found!", Toast.LENGTH_LONG).show();
                        no_network_Layout.setVisibility(View.GONE);
                        grid_layout.setVisibility(View.GONE);
                        no_item_Layout.setVisibility(View.VISIBLE);
                    }

                } else {

                    no_item_Layout.setVisibility(View.GONE);
                    grid_layout.setVisibility(View.GONE);
                    no_network_Layout.setVisibility(View.VISIBLE);

                }

            }
        }, 1000);
    }

    private void getFavoriteList() {

        DatabaseHelper db = DatabaseHelper.getInstance(getContext());
        dataRecipe.clear();
        dataRecipe.addAll(db.getAllRecipes());

        adapterSpecial.setRecipeList(db.getAllRecipes());
        //db.close();

    }

    @Override
    public void onResume() {
        //getFavoriteList();
        BindData();
        super.onResume();

    }
}