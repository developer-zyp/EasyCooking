package com.proton.easycooking.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.proton.easycooking.AdMob;
import com.proton.easycooking.AppTools;
import com.proton.easycooking.InputFilterMinMax;
import com.proton.easycooking.R;


public class CaloriesCalFragment extends Fragment {

    LinearLayout layout_calculate, layout_result;

    Button btn_calculate, btn_result;
    RadioButton rdo_male, rdo_female;
    TextInputEditText edt_Years, edt_Feet, edt_Inches, edt_Pounds, edt_Activity;
    TextView tv_Normal, tv_LoseWeight, tv_MoreLoseWeight;

    int pounds = 0, inches = 0, age = 0;
    int AMR = 0;
    double ACT = 0.0;

    int index = -1;

    String[] cal_array = new String[]{
            "Little or no exercise", "Light exercise 1-3 days/week",
            "Moderate exercise 3-5 days/week", "Hard exercise 6-7 days/week"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_calories_cal, container, false);

        layout_calculate = (LinearLayout) fragmentView.findViewById(R.id.layout_calculate);
        layout_result = (LinearLayout) fragmentView.findViewById(R.id.layout_result);

        rdo_male = (RadioButton) fragmentView.findViewById(R.id.rdo_male);
        rdo_female = (RadioButton) fragmentView.findViewById(R.id.rdo_female);

        edt_Years = (TextInputEditText) fragmentView.findViewById(R.id.edt_years);
        edt_Feet = (TextInputEditText) fragmentView.findViewById(R.id.edt_feet);
        edt_Inches = (TextInputEditText) fragmentView.findViewById(R.id.edt_inches);
        edt_Pounds = (TextInputEditText) fragmentView.findViewById(R.id.edt_pounds);
        edt_Activity = (TextInputEditText) fragmentView.findViewById(R.id.edt_Activity);

        edt_Years.setFilters(new InputFilter[]{new InputFilterMinMax(1, 60)});
        edt_Feet.setFilters(new InputFilter[]{new InputFilterMinMax(4, 7)});
        edt_Inches.setFilters(new InputFilter[]{new InputFilterMinMax(1, 11)});
        edt_Pounds.setFilters(new InputFilter[]{new InputFilterMinMax(1, 250)});

        tv_Normal = (TextView) fragmentView.findViewById(R.id.tv_normalResult);
        tv_LoseWeight = (TextView) fragmentView.findViewById(R.id.tv_loseResult);
        tv_MoreLoseWeight = (TextView) fragmentView.findViewById(R.id.tv_moreLoseResult);

//        Sedentary = BMR X 1.2 (little or no exercise, desk job)
//        Lightly active = BMR X 1.375 (light exercise/sports 1-3 days/wk)
//        Mod. active = BMR X 1.55 (moderate exercise/sports 3-5 days/wk)
//        Very active = BMR X 1.725 (hard exercise/sports 6-7 days/wk)
//        Extr. active = BMR X 1.9 (hard daily exercise/sports & physical job or 2X day training, i.e marathon, contest etc.)

        cal_array = getResources().getStringArray(R.array.cal_array);

        edt_Activity.setOnClickListener(this::showActivityDialog);

        btn_calculate = (Button) fragmentView.findViewById(R.id.btn_cal_calculate);
        btn_calculate.setOnClickListener(view -> {

            if (edt_Years.getText().toString().equals("") ||
                    edt_Feet.getText().toString().equals("") ||
                    edt_Pounds.getText().toString().equals("") ||
                    ACT == 0.0) {
                AppTools.showAlertDialog(getContext(), "", getString(R.string.cal_dialog_need));
                return;

            }
            pounds = Integer.parseInt(edt_Pounds.getText().toString());
            age = Integer.parseInt(edt_Years.getText().toString());
            if (age < 15 || pounds < 50) {
                AppTools.showAlertDialog(getContext(), "", getString(R.string.cal_dialog_invalid));
                return;
            }

            AdMob.showRewardedAd(getContext(), "admob", adClosed -> {
            });

            new Handler().postDelayed(() -> {

                inches = (Integer.parseInt(edt_Feet.getText().toString()) * 12) +
                        Integer.parseInt(edt_Inches.getText().toString().equals("") ? "0" : edt_Inches.getText().toString());

                //Active Metabolic Rate
                AMR = (int) Math.round(calculateBMR(pounds, inches, age) * ACT);

                tv_Normal.setText(String.valueOf(AMR));
                tv_LoseWeight.setText(String.valueOf(Math.round(AMR * 0.8)));
                tv_MoreLoseWeight.setText(String.valueOf(Math.round(AMR * 1.2)));

                layout_calculate.setVisibility(View.GONE);
                layout_result.setVisibility(View.VISIBLE);

            }, 500);

        });

        btn_result = (Button) fragmentView.findViewById(R.id.btn_cal_result);
        btn_result.setOnClickListener(view -> {
            AppTools.showProgressDialog(getContext(), "Please wait ...");
            new Handler().postDelayed(() -> {
                layout_result.setVisibility(View.GONE);
                layout_calculate.setVisibility(View.VISIBLE);
                AppTools.hideProgressDialog();

            }, 500);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Activities");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(cal_array, index, (dialogInterface, i) -> {
            index = i;
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

        });
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            if (index >= 0) {
                ACT = ACT_tmp[0];
                ((EditText) v).setText(cal_array[index]);
            }
            dialogInterface.dismiss();
        });
        builder.show();
    }

}