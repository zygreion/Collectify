package com.example.collectify.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.collectify.R;

import com.bumptech.glide.Glide;
import com.example.collectify.model.StampModel;

import java.util.List;

import androidx.annotation.NonNull;

public class StampAdapter extends RecyclerView.Adapter<StampAdapter.ViewHolder> {

    private Context context;
    private List<StampModel> stampList;

    public StampAdapter(Context context, List<StampModel> stampList) {
        this.context = context;
        this.stampList = stampList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stamp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StampModel item = stampList.get(position);

        holder.tvStampName.setText(item.name);
        Glide.with(context)
                .load(item.imageUrl)
                .placeholder(R.drawable.loading)
                .into(holder.imgStamp);

        // Overlay jika belum dikoleksi
        holder.darkOverlay.setVisibility(!item.isScanned ? View.VISIBLE : View.GONE);

        // Tambahkan klik listener
        holder.imgStamp.setOnClickListener(v -> {
            Intent intent = new Intent(context, com.example.collectify.activities.StampDetailActivity.class);
            intent.putExtra("stamp", item);
            context.startActivity(intent);
        });
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
