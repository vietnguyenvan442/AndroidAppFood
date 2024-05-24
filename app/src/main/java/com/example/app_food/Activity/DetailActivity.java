package com.example.app_food.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import com.bumptech.glide.Glide;
import com.example.app_food.Domain.Foods;
import com.example.app_food.Helper.ManagmentCart;
import com.example.app_food.R;
import com.example.app_food.databinding.ActivityDetailBinding;

import eightbitlab.com.blurview.RenderScriptBlur;

public class DetailActivity extends BaseActivity {
    private ActivityDetailBinding binding;
    private Foods object;
    private int num = 1;
    private ManagmentCart managmentCart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        managmentCart = new ManagmentCart(this);
        getBundleExtra();
        setVariable();
        setBlurEffect();
    }

    private void setBlurEffect() {
        float radius = 10f;
        View decorView = (this).getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windownBackground = decorView.getBackground();

        binding.blurView.setupWith(rootView, new RenderScriptBlur(this)) // or RenderEffectBlur
                .setFrameClearDrawable(windownBackground) // Optional
                .setBlurRadius(radius);

        binding.blurView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        binding.blurView.setClipToOutline(true);

        binding.blurView2.setupWith(rootView, new RenderScriptBlur(this)) // or RenderEffectBlur
                .setFrameClearDrawable(windownBackground) // Optional
                .setBlurRadius(radius);

        binding.blurView2.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        binding.blurView2.setClipToOutline(true);
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());

        Glide.with(DetailActivity.this)
                .load(object.getImagePath())
                .into(binding.pic);

        binding.priceTxt.setText("$"+object.getPrice());
        binding.titleTxt.setText(object.getTitle());
        binding.descriptionTxt.setText(object.getDescription());
        binding.ratingTxt.setText(object.getStar()+" Rating");
        binding.ratingBar.setRating((float) object.getStar());
        binding.totalTxt.setText(num * object.getPrice()+ "$");

        binding.plusBtn.setOnClickListener(v -> {
           if (num >= 1){
               num += 1;
               binding.numTxt.setText(num + "");
               binding.totalTxt.setText((num * object.getPrice()) + "$");
           }
        });
        binding.minusBtn.setOnClickListener(v -> {
            if (num > 1){
                num -= 1;
                binding.numTxt.setText(num + "");
                binding.totalTxt.setText((num * object.getPrice()) + "$");
            }
        });
        binding.addBtn.setOnClickListener(v -> {
            object.setNumberInCart(num);
            managmentCart.insertFood(object);
            startActivity(new Intent(DetailActivity.this, CartActivity.class));
        });
    }

    private void getBundleExtra() {
        object = (Foods) getIntent().getSerializableExtra("object");
    }
}