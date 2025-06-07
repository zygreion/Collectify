package com.example.collectify.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.collectify.R;

import com.bumptech.glide.Glide;
import com.example.collectify.model.CollectionModel;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.collectify.R;
import com.example.collectify.model.CollectionModel;
import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

    private Context context;
    private List<CollectionModel> collectionList;

    public CollectionAdapter(Context context, List<CollectionModel> collectionList) {
        this.context = context;
        this.collectionList = collectionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_collection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CollectionModel item = collectionList.get(position);
        holder.nameTextView.setText(item.name);

        Glide.with(context)
                .load(item.image_url)
                .placeholder(R.drawable.loading)
                .into(holder.imageView);

        // Overlay jika belum dikoleksi
        holder.overlayView.setVisibility(item.sudah_dikoleksi == 0 ? View.VISIBLE : View.GONE);

        // Tambahkan klik listener
        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, com.example.collectify.activities.DescriptionCollectionActivity.class);
            intent.putExtra("collection", item);
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return collectionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        View overlayView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageCollection);
            nameTextView = itemView.findViewById(R.id.nameCollection);
            overlayView = itemView.findViewById(R.id.darkOverlay);
        }
    }
}
