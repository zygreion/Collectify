package com.example.collectify.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collectify.R;
import com.example.collectify.activities.CollectionStampsActivity;
import com.example.collectify.activities.ProfileActivity;
import com.example.collectify.activities.StampDetailActivity;
import com.example.collectify.model.CollectionModel;
import com.example.collectify.model.CollectionStampsModel;
import com.example.collectify.model.StampModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionSectionAdapter extends RecyclerView.Adapter<CollectionSectionAdapter.ViewHolder> {

    private Context context;
    private List<CollectionStampsModel> collectionStampsList;

    public CollectionSectionAdapter(Context context, List<CollectionStampsModel> collectionStampsList) {
        this.context = context;
        this.collectionStampsList = collectionStampsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_collection_section, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CollectionStampsModel item = collectionStampsList.get(position);
        CollectionModel collection = item.collection;
        holder.tvCollectionName.setText(collection.name);

        String collectedStampsStr = "Collected (" + collection.totalStampsCollected + "/" + collection.totalStamps + ")";
        holder.tvCollectedStamps.setText(collectedStampsStr);

        Glide.with(context)
                .load(collection.imageUrl)
                .placeholder(R.drawable.loading)
                .into(holder.imgCollection);

        // Check if stampList is not empty before accessing its elements
        if (item.stampList != null && !item.stampList.isEmpty()) {
            // Replace collection image with stamp previews
            holder.imgCollection.setVisibility(View.GONE);

            holder.btnDetail.setOnClickListener(v -> {
                Intent intent = new Intent(context, CollectionStampsActivity.class);
                // You'll likely want to pass the 'item' or 'collection' ID here
                intent.putExtra("collectionStamps", (Serializable) item);
                context.startActivity(intent);
            });

            // Preview stamps limited to 3
            List<StampModel> limitedStampList = new ArrayList<>();
            int i = 0;
            while (i < item.stampList.size() && i < 3) {
                limitedStampList.add(item.stampList.get(i));
                i++;
            }

            holder.rvStampPreview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            StampPreviewAdapter adapter = new StampPreviewAdapter(context, limitedStampList);
            holder.rvStampPreview.setAdapter(adapter);
        } else {
            holder.btnDetail.setEnabled(false);
        }
    }


    @Override
    public int getItemCount() {
        return collectionStampsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCollection;
        TextView tvCollectionName;
        TextView tvCollectedStamps;
        Button btnDetail;
        RecyclerView rvStampPreview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCollection = itemView.findViewById(R.id.img_collection);
            tvCollectionName = itemView.findViewById(R.id.tv_collection_name);
            tvCollectedStamps = itemView.findViewById(R.id.tv_collected_stamps);
            btnDetail = itemView.findViewById(R.id.btn_detail);
            rvStampPreview = itemView.findViewById(R.id.rv_stamp_preview);
        }
    }
}
