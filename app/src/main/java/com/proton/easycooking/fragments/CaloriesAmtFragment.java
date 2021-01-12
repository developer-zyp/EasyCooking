package com.proton.easycooking.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.proton.easycooking.APIClient;
import com.proton.easycooking.APIInterface;
import com.proton.easycooking.GlobalClass;
import com.proton.easycooking.MainActivity;
import com.proton.easycooking.NetworkTask;
import com.proton.easycooking.R;
import com.proton.easycooking.adapters.AdapterCaloriesAmt;
import com.proton.easycooking.models.Category;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CaloriesAmtFragment extends Fragment {

    private LinearLayout rootLayout;
    private RelativeLayout grid_layout;

    private ArrayList<Category> dataCaloriesAmt;
    RecyclerView recyclerView;
    AdapterCaloriesAmt adapterCaloriesAmt;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_calories_amt, container, false);

        rootLayout = (LinearLayout) fragmentView.findViewById(R.id.rootLayout);

//        if (Config.ENABLE_RTL_MODE) {
//            rootLayout.setRotationY(180);
//        }

        grid_layout = (RelativeLayout) fragmentView.findViewById(R.id.grid_layout);

        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        dataCaloriesAmt = new ArrayList<Category>();
        adapterCaloriesAmt = new AdapterCaloriesAmt(getActivity(), dataCaloriesAmt);
        recyclerView.setAdapter(adapterCaloriesAmt);

        progressDialog = new ProgressDialog(getContext(), R.style.MyProgressDialogStyle);
        progressDialog.setMessage("Loading ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        new NetworkTask().execute(APIClient.BASE_URL);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (NetworkTask.checkServerConnection) {
                    getCaloriesAmtList();
                    progressDialog.dismiss();

                } else {
                    progressDialog.dismiss();
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setTitle("Warning!");
                    alertDialog.setMessage("No Internet Connection.");
                    alertDialog.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MainActivity.fragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, new CaloriesAmtFragment())
                                    //.addToBackStack(null)
                                    .commit();
                        }
                    });
                    alertDialog.show();

                }

            }
        }, 1000);


        return fragmentView;
    }

    private void getCaloriesAmtList() {
        Call<List<Category>> callRecipe = APIClient.getService().create(APIInterface.class).getCaloriesAmtById(GlobalClass.categoryId);

        callRecipe.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.body() != null) {
                    adapterCaloriesAmt.setCaloriesAmtList(response.body());

                    progressDialog.dismiss();
                    //Toast.makeText(getContext(), dataCategory.size() + " data is received!", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "ERROR : Fail to receive data!", Toast.LENGTH_SHORT).show();
                System.out.println("Fail: " + t.getMessage());

            }
        });



    }
}