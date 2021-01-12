package com.proton.easycooking.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.proton.easycooking.fragments.CaloriesCalFragment;
import com.proton.easycooking.fragments.CategoryFragment;
import com.proton.easycooking.R;

public class TabCaloriesCalcFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_tab_calories_calc, container, false);

        viewPager = (ViewPager) fragmentView.findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager(),1));

        tabLayout = (TabLayout) fragmentView.findViewById(R.id.tabs);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mainActivity.setupNavigationDrawer(toolbar);
    }

    class MyAdapter extends FragmentPagerAdapter{


        public MyAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new CaloriesCalFragment();
                case 1:
                    return new CategoryFragment(2);
            }
            return null;
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