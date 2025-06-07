package com.example.collectify.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collectify.R;
import com.example.collectify.activities.MerchandiseActivity;
import com.example.collectify.db.SupabaseClient;
import com.example.collectify.model.MerchandiseModel;
import com.example.collectify.utils.SessionManager;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class MerchandiseAdapter extends RecyclerView.Adapter<MerchandiseAdapter.ViewHolder> {

    private final List<MerchandiseModel> merchandiseList;
    private final Context context;

    public MerchandiseAdapter(Context context, List<MerchandiseModel> merchandiseList) {
        this.context = context;
        this.merchandiseList = merchandiseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_merchandise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MerchandiseModel item = merchandiseList.get(position);
        holder.textName.setText(item.name);
        holder.textStock.setText("Stok: " + item.stock);

        Glide.with(context)
                .load(item.imageUrl)
                .placeholder(R.drawable.img)
                .override(300, 300)
                .into(holder.image);

        holder.buttonTukar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_exchange, null);
            builder.setView(dialogView);

            ImageView dialogImage = dialogView.findViewById(R.id.dialogImage);
            TextView dialogStock = dialogView.findViewById(R.id.dialogStock);
            TextView dialogStempel = dialogView.findViewById(R.id.dialogStempel);
            Button buttonConfirm = dialogView.findViewById(R.id.buttonConfirmTukar);

            // Isi data dari item
            Glide.with(context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.img)
                    .override(300, 300)
                    .into(dialogImage);

            dialogStock.setText("Stock tersisa: " + item.stock);
            dialogStempel.setText("Stempel yang dibutuhkan: 2");

            AlertDialog dialog = builder.create();
            dialog.show();

            buttonConfirm.setOnClickListener(confirmView -> {
                SessionManager sessionManager = new SessionManager(context);
                String userId = sessionManager.getUserId();

                new Thread(() -> {
                    try {
                        String result = SupabaseClient.exchangeMerchandise(userId, item.id);


                        ((android.app.Activity) context).runOnUiThread(() -> {
                            if (result.equals("success")) {
                                item.stock -= 1; // Update lokal
                                notifyItemChanged(holder.getAdapterPosition());
                                dialog.dismiss();
                                Toast.makeText(context, "Penukaran berhasil!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Penukaran gagal: " + result, Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        ((android.app.Activity) context).runOnUiThread(() ->
                                Toast.makeText(context, "Terjadi kesalahan saat menukar!", Toast.LENGTH_SHORT).show());
                    }
                }).start();
            });

        });
    }


    @Override
    public int getItemCount() {
        return merchandiseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView textName, textStock;
        Button buttonTukar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            textName = itemView.findViewById(R.id.textName);
            textStock = itemView.findViewById(R.id.textStock);
            buttonTukar = itemView.findViewById(R.id.buttonTukar);
        }
    }
}