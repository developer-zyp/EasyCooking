package com.proton.easycooking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.proton.easycooking.MainActivity;
import com.proton.easycooking.R;
import com.proton.easycooking.fragments.TodaySpecialFragment;
import com.proton.easycooking.models.Category;

import java.util.ArrayList;
import java.util.List;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.ViewHolder> {

    private final Context context;
    private final int layoutId;
    private final int selectFragment;
    private List<Category> categoryList = new ArrayList<>();
    private Category itemCategory;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        ImageView imageView;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.textViewName = (TextView) view.findViewById(R.id.txtName);
            this.imageView = (ImageView) view.findViewById(R.id.imageView);
            this.cardView = (CardView) view.findViewById(R.id.cv_Category);
        }
    }

    public AdapterCategory(Context mContext, int layoutId, int selectFragment) {
        this.context = mContext;
        this.layoutId = layoutId;
        this.selectFragment = selectFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        itemCategory = categoryList.get(position);

        holder.textViewName.setText(itemCategory.getCategoryName());

        if (itemCategory.getCategoryImage().length() > 0) {
            Glide.with(context).load(itemCategory.getCategoryImage())
                    .placeholder(R.drawable.simple_img1)
                    .into(holder.imageView);
        }

        holder.cardView.setOnClickListener(view -> {

            itemCategory = categoryList.get(position);
            int catId = Integer.parseInt(itemCategory.getCategoryId());

            FragmentManager fragmentManager = MainActivity.fragmentManager;
            switch (selectFragment) {
                case 1:
                case 3:
                case 4:
                    fragmentManager.beginTransaction()
                            .add(R.id.fragment_container, new TodaySpecialFragment(catId, 0))
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack(null)
                            .commit();
                    break;
                case 2:
                    fragmentManager.beginTransaction()
                            .add(R.id.fragment_container, new TodaySpecialFragment(0, catId))
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack(null)
                            .commit();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.fragment_container, new CaloriesAmtFragment(catId))
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                        .addToBackStack(null)
//                        .commit();
                    break;
            }

        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

}
