package com.proton.easycooking.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.textfield.TextInputEditText;
import com.proton.easycooking.Config;
import com.proton.easycooking.InputFilterMinMax;
import com.proton.easycooking.R;


public class CaloriesCalFragment extends Fragment {

    LinearLayout layout_calculate;
    RelativeLayout layout_result;

    Button btn_calculate, btn_result;
    RadioButton rdo_male, rdo_female;
    TextInputEditText edt_Years, edt_Feet, edt_Inches, edt_Pounds, edt_Activity;
    TextView tv_Normal, tv_LoseWeight, tv_MoreLoseWeight;

    int pounds = 0, inches = 0, age = 0;
    int AMR = 0;
    double ACT = 0.0;

    final String[] array = new String[]{
            "Little or no exercise", "Light exercise 1-3 days/week",
            "Moderate exercise 3-5 days/week", "Hard exercise 6-7 days/week"
    };

    private RewardedAd rewardedAd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_calories_cal, container, false);

        createAndLoadRewardedAd(getActivity().getResources().getString(R.string.adMob_reward_id));

        layout_calculate = (LinearLayout) fragmentView.findViewById(R.id.layout_calculate);
        layout_result = (RelativeLayout) fragmentView.findViewById(R.id.layout_result);

        rdo_male = (RadioButton) fragmentView.findViewById(R.id.rdo_male);
        rdo_female = (RadioButton) fragmentView.findViewById(R.id.rdo_female);

        edt_Years = (TextInputEditText) fragmentView.findViewById(R.id.edt_years);
        edt_Feet = (TextInputEditText) fragmentView.findViewById(R.id.edt_feet);
        edt_Inches = (TextInputEditText) fragmentView.findViewById(R.id.edt_inches);
        edt_Pounds = (TextInputEditText) fragmentView.findViewById(R.id.edt_pounds);
        edt_Activity = (TextInputEditText) fragmentView.findViewById(R.id.edt_Activity);

        edt_Years.setFilters(new InputFilter[]{new InputFilterMinMax(1, 80)});
        edt_Feet.setFilters(new InputFilter[]{new InputFilterMinMax(4, 7)});
        edt_Inches.setFilters(new InputFilter[]{new InputFilterMinMax(1, 11)});
        edt_Pounds.setFilters(new InputFilter[]{new InputFilterMinMax(1, 350)});

        tv_Normal = (TextView) fragmentView.findViewById(R.id.tv_normalResult);
        tv_LoseWeight = (TextView) fragmentView.findViewById(R.id.tv_loseResult);
        tv_MoreLoseWeight = (TextView) fragmentView.findViewById(R.id.tv_moreLoseResult);

//        Sedentary = BMR X 1.2 (little or no exercise, desk job)
//        Lightly active = BMR X 1.375 (light exercise/sports 1-3 days/wk)
//        Mod. active = BMR X 1.55 (moderate exercise/sports 3-5 days/wk)
//        Very active = BMR X 1.725 (hard exercise/sports 6-7 days/wk)
//        Extr. active = BMR X 1.9 (hard daily exercise/sports & physical job or 2X day training, i.e marathon, contest etc.)

        edt_Activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showActivityDialog(view);
            }
        });

        btn_calculate = (Button) fragmentView.findViewById(R.id.btn_cal_calculate);
        btn_calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_Years.getText().toString().equals("") ||
                        edt_Feet.getText().toString().equals("") ||
                        edt_Pounds.getText().toString().equals("") ||
                        ACT == 0.0) {
                    showErrorDialog("Warning!", "Please fill the details.");
                    return;

                }
                pounds = Integer.parseInt(edt_Pounds.getText().toString());
                age = Integer.parseInt(edt_Years.getText().toString());
                if (age < 15 || pounds < 50) {
                    showErrorDialog("Warning!", "Invalid input.");
                    return;
                }

                ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.MyProgressDialogStyle);
                progressDialog.setMessage("Please wait ...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        inches = (Integer.parseInt(edt_Feet.getText().toString()) * 12) +
                                Integer.parseInt(edt_Inches.getText().toString().equals("") ? "0" : edt_Inches.getText().toString());

                        //Active Metabolic Rate
                        AMR = (int) Math.round(calculateBMR(pounds, inches, age) * ACT);

                        tv_Normal.setText(String.valueOf(AMR));
                        tv_LoseWeight.setText(String.valueOf(Math.round(AMR * 0.8)));
                        tv_MoreLoseWeight.setText(String.valueOf(Math.round(AMR * 0.6)));

                        if (Config.ENABLE_ADMOB_REWARDED_ADS) {
                            showRewardedAd(1);
                            createAndLoadRewardedAd(getActivity().getResources().getString(R.string.adMob_reward_id));
                        } else {
                            layout_calculate.setVisibility(View.GONE);
                            layout_result.setVisibility(View.VISIBLE);
                        }

                        progressDialog.dismiss();

                    }
                }, 2000);

            }
        });

        btn_result = (Button) fragmentView.findViewById(R.id.btn_cal_result);
        btn_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.MyProgressDialogStyle);
                progressDialog.setMessage("Please wait ...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Config.ENABLE_ADMOB_REWARDED_ADS) {
                            showRewardedAd(0);
                            createAndLoadRewardedAd(getActivity().getResources().getString(R.string.adMob_reward_id));
                        } else {
                            layout_result.setVisibility(View.GONE);
                            layout_calculate.setVisibility(View.VISIBLE);
                        }
                        progressDialog.dismiss();

                    }
                }, 2000);

            }
        });

        return fragmentView;
    }

    //Basal Metabolic Rate
    public double calculateBMR(int Pounds, int Inches, int Age) {

        double result = 0.0;
        if (rdo_male.isChecked()) {
            //result = 66 + (6.23 * Pounds) + (12.7 * Inches) - (6.8 * Age);
            result = 5 + (10 * Pounds * 0.453592) + (6.25 * Inches * 2.54) - (5 * Age);

        } else if (rdo_female.isChecked()) {
            //result = 665 + (4.35 * Pounds) + (4.7 * Inches) - (4.7 * Age);
            result = (10 * Pounds * 0.453592) + (6.25 * Inches * 2.54) - (5 * Age) - 161;
        }

        return result;

    }

    private void showActivityDialog(final View v) {
        final double[] ACT_tmp = {0.0};
        final int[] index = {-1};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Activities");
        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                index[0] = i;
                switch (i) {
                    case 0:
                        ACT_tmp[0] = 1.2;
                        break;
                    case 1:
                        ACT_tmp[0] = 1.375;
                        break;
                    case 2:
                        ACT_tmp[0] = 1.55;
                        break;
                    case 3:
                        ACT_tmp[0] = 1.725;
                        break;
                }

            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ACT = ACT_tmp[0];
                if (index[0] >= 0) ((EditText) v).setText(array[index[0]]);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void showErrorDialog(String title, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        //dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    public void createAndLoadRewardedAd(String adUnitId) {
        rewardedAd = new RewardedAd(getContext(), adUnitId);
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }

    public void showRewardedAd(int page) {
        if (rewardedAd.isLoaded()) {
            RewardedAdCallback adCallback = new RewardedAdCallback() {
                @Override
                public void onRewardedAdOpened() {
                    // Ad opened.
                }

                @Override
                public void onRewardedAdClosed() {
                    // Ad closed.
                }

                @Override
                public void onUserEarnedReward(@NonNull RewardItem reward) {
                    // User earned reward.
                    if (page == 1) {
                        layout_calculate.setVisibility(View.GONE);
                        layout_result.setVisibility(View.VISIBLE);
                    } else {
                        layout_result.setVisibility(View.GONE);
                        layout_calculate.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onRewardedAdFailedToShow(AdError adError) {
                    // Ad failed to display.
                }
            };
            rewardedAd.show(getActivity(), adCallback);
        } else {
            Toast.makeText(getContext(), "Connection fail!\n Please retry.", Toast.LENGTH_LONG).show();
        }
    }

}