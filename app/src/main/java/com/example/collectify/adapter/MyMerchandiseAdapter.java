package com.example.collectify.adapter;

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
import com.example.collectify.model.MyMerchandiseModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MyMerchandiseAdapter extends RecyclerView.Adapter<MyMerchandiseAdapter.ViewHolder> {

    private final List<MyMerchandiseModel> myMerchandiseList;
    private final Context context;

    public MyMerchandiseAdapter(Context context, List<MyMerchandiseModel> myMerchandiseList) {
        this.context = context;
        this.myMerchandiseList = myMerchandiseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_merchandise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyMerchandiseModel item = myMerchandiseList.get(position);
        holder.textName.setText(item.name);

        // Format tanggal agar lebih mudah dibaca
        holder.textExchangedAt.setText("Ditukar pada: " + formatDateTime(item.exchangedAt));

        Glide.with(context)
                .load(item.imageUrl)
                .placeholder(R.drawable.img)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return myMerchandiseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView textName, textExchangedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            textName = itemView.findViewById(R.id.textName);
            textExchangedAt = itemView.findViewById(R.id.textExchangedAt);
        }
    }

    // Fungsi helper untuk memformat tanggal
    private String formatDateTime(String isoString) {
        if (isoString == null || isoString.isEmpty()) {
            return "N/A";
        }
        try {
            // Input format dari Supabase (ISO 8601)
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = inputFormat.parse(isoString);

            // Output format yang diinginkan
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm", new Locale("id", "ID"));
            outputFormat.setTimeZone(TimeZone.getDefault()); // Sesuaikan dengan timezone lokal
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            // Jika parsing gagal, kembalikan string asli tanpa bagian T
            return isoString.split("T")[0];
        }
    }
}