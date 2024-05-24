package com.example.app_food.Adapter;

import android.app.Activity;
import android.content.Context;
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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.app_food.Activity.CartActivity;
import com.example.app_food.Domain.Foods;
import com.example.app_food.Helper.ChangeNumberItemsListener;
import com.example.app_food.Helper.ManagmentCart;
import com.example.app_food.R;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    ArrayList<Foods> listItemSelected;
    private ManagmentCart managmentCart;
    ChangeNumberItemsListener changeNumberItemsListener;

    public CartAdapter(ArrayList<Foods> listItemSelected, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.listItemSelected = listItemSelected;
        this.managmentCart = new ManagmentCart(context);
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        float radius = 10f;
        View decorView = ((Activity)holder.itemView.getContext()).getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windownBackground = decorView.getBackground();

        holder.blurView.setupWith(rootView, new RenderScriptBlur(holder.itemView.getContext())) // or RenderEffectBlur
                .setFrameClearDrawable(windownBackground) // Optional
                .setBlurRadius(radius);

        holder.blurView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        holder.blurView.setClipToOutline(true);

        Glide.with(holder.itemView.getContext())
                .load(listItemSelected.get(position).getImagePath())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.img);

        holder.titleTxt.setText(listItemSelected.get(position).getTitle());
        holder.totalEachItem.setText("$"+listItemSelected.get(position).getNumberInCart() * listItemSelected.get(position).getPrice());
        holder.feeEachItem.setText(listItemSelected.get(position).getNumberInCart() + " * $" + (listItemSelected.get(position).getPrice()));

        holder.num.setText(String.valueOf(listItemSelected.get(position).getNumberInCart()));

        holder.plusItem.setOnClickListener(v ->
            managmentCart.plusNumberItem(listItemSelected, position, () -> {
                changeNumberItemsListener.change();
                notifyDataSetChanged();
            }));
        holder.minusItem.setOnClickListener(v ->
                managmentCart.minusNumberItem(listItemSelected, position, () -> {
                    changeNumberItemsListener.change();
                    notifyDataSetChanged();
                }));
        holder.removeCart.setOnClickListener(v ->
                managmentCart.removeCartItem(listItemSelected, position, () -> {
                    changeNumberItemsListener.change();
                    notifyDataSetChanged();
                }));
    }

    @Override
    public int getItemCount() {
        return listItemSelected.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt, feeEachItem, plusItem, minusItem, removeCart;
        ImageView img;
        TextView totalEachItem, num;
        BlurView blurView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            plusItem = itemView.findViewById(R.id.plusBtn);
            minusItem = itemView.findViewById(R.id.minusBtn);
            img = itemView.findViewById(R.id.img);
            totalEachItem = itemView.findViewById(R.id.totalEachItem);
            num = itemView.findViewById(R.id.numTxt);
            blurView = itemView.findViewById(R.id.blurView);
            removeCart = itemView.findViewById(R.id.removeCart);
        }
    }
}
