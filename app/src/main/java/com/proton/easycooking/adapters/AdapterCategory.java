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
import com.proton.easycooking.fragments.CaloriesAmtFragment;
import com.proton.easycooking.fragments.TodaySpecialFragment;
import com.proton.easycooking.GlobalClass;
import com.proton.easycooking.MainActivity;
import com.proton.easycooking.R;
import com.proton.easycooking.models.Category;

import java.util.List;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.ViewHolder> {

    Category itemCategory;
    private final Context context;
    private List<Category> arrayCategory;
    private int selectFragment = 1;

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

    public AdapterCategory(Context mContext, List<Category> arrayItemCategory, int selectFragment) {
        this.context = mContext;
        this.arrayCategory = arrayItemCategory;
        this.selectFragment = selectFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_category, parent, false);

        GlobalClass.categoryId = 0;

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        itemCategory = arrayCategory.get(position);

        holder.textViewName.setText(itemCategory.getCategoryName());

        if (itemCategory.getCategoryImage().length() > 0) {
            Glide.with(context).load(itemCategory.getCategoryImage())
                    .placeholder(R.drawable.simple_img1)
                    .into(holder.imageView);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                itemCategory = arrayCategory.get(position);
                int catId = Integer.parseInt(itemCategory.getCategoryId());
//                Toast.makeText(context, "Category Id : " + catId + "\nName : " +
//                        itemCategory.getCategoryName(), Toast.LENGTH_LONG).show();

                GlobalClass.categoryId = catId;

                FragmentManager fragmentManager = MainActivity.fragmentManager;
                if (selectFragment == 1) {

                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, new TodaySpecialFragment())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack(null)
                            .commit();
                }
                else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, new CaloriesAmtFragment())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack(null)
                            .commit();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayCategory.size();
    }

    public void setCategoryList(List<Category> categoryList) {
        arrayCategory = categoryList;
        notifyDataSetChanged();
    }

}
