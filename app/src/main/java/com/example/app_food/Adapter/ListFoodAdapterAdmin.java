package com.example.app_food.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.app_food.Activity.EditFoodActivity;
import com.example.app_food.Domain.Foods;
import com.example.app_food.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class ListFoodAdapterAdmin extends RecyclerView.Adapter<ListFoodAdapterAdmin.ViewHolder> {
    private List<Foods> list;
    private Context context;

    public ListFoodAdapterAdmin(List<Foods> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ListFoodAdapterAdmin.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListFoodAdapterAdmin.ViewHolder holder, int position) {
        Foods food = list.get(position);
        holder.nameTxt.setText(food.getTitle());
        holder.priceTxt.setText("$" + food.getPrice());
        holder.timeTxt.setText(food.getTimeValue() + " min");
        holder.starTxt.setText("" + food.getStar());

        float radius = 10f;
        View decorView = ((Activity) holder.itemView.getContext()).getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        holder.blurView.setupWith(rootView, new RenderScriptBlur(holder.itemView.getContext()))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius);

        holder.blurView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        holder.blurView.setClipToOutline(true);

        Glide.with(context)
                .load(food.getImagePath())
                .transform(new RoundedCorners(30))
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void deleteFood(int foodId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Foods").child(String.valueOf(foodId));
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Food deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete food", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        BlurView blurView;
        ImageView img;
        TextView nameTxt, starTxt, timeTxt, priceTxt;
        TextView btUpdate, btDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            starTxt = itemView.findViewById(R.id.starTxt);
            timeTxt = itemView.findViewById(R.id.timeTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            btUpdate = itemView.findViewById(R.id.btUpdate);
            btDelete = itemView.findViewById(R.id.btDelete);
            blurView = itemView.findViewById(R.id.blurView);

            btUpdate.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Foods food = list.get(position);
                    Intent intent = new Intent(context, EditFoodActivity.class);
                    intent.putExtra("food", food);
                    context.startActivity(intent);
                    notifyDataSetChanged();
                }
            });

            btDelete.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    new AlertDialog.Builder(context)
                            .setTitle("Thông báo xóa")
                            .setMessage("Bạn có chắc chắn muốn xóa không?")
                            .setIcon(R.drawable.icon_remove)
                            .setPositiveButton("Yes", (dialog, which) -> {
                                Foods food = list.get(position);
                                deleteFood(food.getId());
                                list.remove(position);
                                notifyItemRemoved(position);
                            })
                            .setNegativeButton("No", null)
                            .create()
                            .show();
                }
            });
        }
    }
}
