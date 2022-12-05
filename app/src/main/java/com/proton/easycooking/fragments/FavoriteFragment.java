package com.proton.easycooking.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.proton.easycooking.AppTools;
import com.proton.easycooking.DatabaseHelper;
import com.proton.easycooking.R;
import com.proton.easycooking.adapters.AdapterSpecial;
import com.proton.easycooking.models.Recipe;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment {

    //region Variables
    private SwipeRefreshLayout swipeRefreshLayout = null;
    private ArrayList<Recipe> dataRecipe;
    RecyclerView recyclerView;
    AdapterSpecial adapterSpecial;

    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_favorite, container, false);

        //SwipeRefreshLayout
        swipeRefreshLayout = fragmentView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        //RecyclerView
        recyclerView = fragmentView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        dataRecipe = new ArrayList<>();
        adapterSpecial = new AdapterSpecial(getActivity(), dataRecipe);
        recyclerView.setAdapter(adapterSpecial);

        swipeRefreshLayout.setRefreshing(true);
        BindData();

        swipeRefreshLayout.setOnRefreshListener(this::BindData);

        return fragmentView;
    }

    private void BindData() {
        new Handler().postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            getFavoriteList();
            if (dataRecipe.size() <= 0) {
                AppTools.showToast(getContext(), "No Favorites found!");
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