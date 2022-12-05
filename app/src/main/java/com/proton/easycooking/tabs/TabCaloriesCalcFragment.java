package com.proton.easycooking.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.proton.easycooking.AppTools;
import com.proton.easycooking.R;
import com.proton.easycooking.fragments.CaloriesAmtFragment;
import com.proton.easycooking.fragments.CaloriesCalFragment;

public class TabCaloriesCalcFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_tab_calories_calc, container, false);

        viewPager = (ViewPager) fragmentView.findViewById(R.id.viewpager);
        PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager(), 1);
        viewPager.setAdapter(pagerAdapter);

        tabLayout = (TabLayout) fragmentView.findViewById(R.id.tabs);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    AppTools.showToast(getContext(), getString(R.string.gram_kt));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return fragmentView;
    }

    class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new CaloriesCalFragment();
                case 1:
                    return new CaloriesAmtFragment();
                default:
                    return new CaloriesCalFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.cal_calculator);
                case 1:
                    return getString(R.string.cal_amount);
            }
            return super.getPageTitle(position);
        }
    }

}