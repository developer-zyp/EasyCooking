package com.proton.easycooking.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.proton.easycooking.AdMob;
import com.proton.easycooking.R;
import com.proton.easycooking.models.NoEatItem;

import java.util.List;

public class AdapterNoEat extends RecyclerView.Adapter<AdapterNoEat.ViewHolder> {

    private final Context context;
    private List<NoEatItem> noEatItemList;
    NoEatItem itemNoEat;

    public AdapterNoEat(Context mContext, List<NoEatItem> noEatItemList) {
        this.context = mContext;
        this.noEatItemList = noEatItemList;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        itemNoEat = noEatItemList.get(position);

        holder.textItem1.setText(itemNoEat.getItems().split("&")[0]);
        holder.textItem2.setText(itemNoEat.getItems().split("&")[1]);
        holder.textAction.setText(itemNoEat.getAction());

        holder.cardView.setOnClickListener(view -> {

            itemNoEat = noEatItemList.get(position);

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View dialogView = inflater.inflate(R.layout.dialog_noeat, null);
            final View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_noeat, null);
            dialogBuilder.setView(dialogView);
            ImageView imgEmo = (ImageView) dialogView.findViewById(R.id.img_emoji);
            switch (itemNoEat.getStatus()) {
                case "1":
                    Glide.with(context)
                            .load(R.drawable.face_dizzy)
                            .into(imgEmo);
                    break;
                case "2":
                    Glide.with(context)
                            .load(R.drawable.face_woozy)
                            .into(imgEmo);
                    break;
                case "3":
                    Glide.with(context)
                            .load(R.drawable.face_vomiting)
                            .into(imgEmo);
                    break;
                case "4":
                    Glide.with(context)
                            .load(R.drawable.face_weary)
                            .into(imgEmo);
                    break;
                case "5":
                    Glide.with(context)
                            .load(R.drawable.face_zany)
                            .into(imgEmo);
                case "6":
                    Glide.with(context)
                            .load(R.drawable.face_hot)
                            .into(imgEmo);
                case "7":
                    Glide.with(context)
                            .load(R.drawable.face_zany)
                            .into(imgEmo);
                    break;
            }

            TextView textView = (TextView) dialogView.findViewById(R.id.tv_banner);
            Button btnSave = (Button) dialogView.findViewById(R.id.btn_saveme);
            btnSave.setOnClickListener(view1 -> {

                AdMob.showRewardedAd(context, "admob", adClosed -> {
                    Glide.with(context)
                            .load(R.drawable.face_hugging)
                            .placeholder(R.drawable.simple_img1)
                            .into(imgEmo);

                    textView.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.INVISIBLE);

                });
            });

            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();

        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_noeat, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return noEatItemList.size();
    }

    public void setNoEatItemList(List<NoEatItem> noEatItemList) {
        this.noEatItemList = noEatItemList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textItem1;
        TextView textItem2;
        TextView textAction;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.textItem1 = (TextView) view.findViewById(R.id.tv_Item1);
            this.textItem2 = (TextView) view.findViewById(R.id.tv_Item2);
            this.textAction = (TextView) view.findViewById(R.id.tv_Action);
            this.cardView = (CardView) view.findViewById(R.id.cv_NoEat);
        }
    }
}
