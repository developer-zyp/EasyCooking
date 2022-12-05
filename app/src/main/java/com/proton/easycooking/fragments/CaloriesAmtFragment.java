package com.proton.easycooking.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.proton.easycooking.APIClient;
import com.proton.easycooking.APIInterface;
import com.proton.easycooking.AppTools;
import com.proton.easycooking.R;
import com.proton.easycooking.adapters.AdapterCaloriesAmt;
import com.proton.easycooking.models.Category;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CaloriesAmtFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout = null;

    RecyclerView recyclerView;
    AdapterCaloriesAmt adapterCaloriesAmt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_calories_amt, container, false);

        swipeRefreshLayout = fragmentView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        recyclerView = fragmentView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ArrayList<Category> dataCaloriesAmt = new ArrayList<>();
        adapterCaloriesAmt = new AdapterCaloriesAmt(getActivity(), dataCaloriesAmt);
        recyclerView.setAdapter(adapterCaloriesAmt);

        swipeRefreshLayout.setRefreshing(true);
        new Handler().post(() -> {
            if (AppTools.isNetworkAvailable(getContext())) {
                getCaloriesAmtList();
            } else {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();

            }

        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Handler().post(() -> {
                if (AppTools.isNetworkAvailable(getContext())) {
                    getCaloriesAmtList();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "Check your connection!\n Pull to refresh.", Toast.LENGTH_SHORT).show();
                }

            });
        });


        return fragmentView;
    }

    private void getCaloriesAmtList() {
        Call<List<Category>> callRecipe = APIClient.getService().create(APIInterface.class).getCaloriesAmt();
        callRecipe.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapterCaloriesAmt.setCaloriesAmtList(response.body());
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
}