package com.example.collectify.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collectify.R;
import com.example.collectify.activities.StampDetailActivity;
import com.example.collectify.model.CollectionModel;
import com.example.collectify.model.CollectionStampsModel;
import com.example.collectify.model.StampModel;

import java.io.Serializable;
import java.util.List;

public class StampPreviewAdapter extends RecyclerView.Adapter<StampPreviewAdapter.ViewHolder> {

    private Context context;
    private List<StampModel> stampList;

    public StampPreviewAdapter(Context context, List<StampModel> stampList) {
        this.context = context;
        this.stampList = stampList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stamp_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Check if stampList is not empty before accessing its elements
        if (stampList != null && !stampList.isEmpty()) {
            StampModel stamp = stampList.get(position);
            holder.tvStampName.setText(stamp.name);

            Glide.with(context)
                    .load(stamp.imageUrl)
                    .placeholder(R.drawable.loading)
                    .into(holder.imgStamp);


            holder.darkOverlay.setVisibility(!stamp.isScanned ? View.VISIBLE : View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return stampList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgStamp;
        TextView tvStampName;
        View darkOverlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStamp = itemView.findViewById(R.id.img_stamp);
            tvStampName = itemView.findViewById(R.id.tv_stamp_name);
            darkOverlay = itemView.findViewById(R.id.dark_overlay);
        }
    }
}
