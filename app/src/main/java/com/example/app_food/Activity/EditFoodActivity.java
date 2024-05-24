package com.example.app_food.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.app_food.Domain.Foods;
import com.example.app_food.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditFoodActivity extends AppCompatActivity {
    private ImageView img;
    private EditText titleTxt, desTxt, priceTxt, timeTxt, cateTxt;
    private TextView errorImg, errorTitle, errorDes, errorPrice, errorTime, errorCate, Title;
    private AppCompatButton btUpdate;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int PICK_IMAGE = 1;
    private Uri imageUri;
    private Foods currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        initEdit();

        currentFood = (Foods) getIntent().getSerializableExtra("food");
        titleTxt.setText(currentFood.getTitle());
        priceTxt.setText(String.valueOf(currentFood.getPrice()));
        timeTxt.setText(String.valueOf(currentFood.getTimeValue()));
        desTxt.setText(currentFood.getDescription());
        cateTxt.setText(String.valueOf(currentFood.getCategoryId()));

        Glide.with(this)
                .load(currentFood.getImagePath())
                .transform(new RoundedCorners(30))
                .into(img);

        img.setOnClickListener(v -> openGallery());
        btUpdate.setOnClickListener(v -> updateFoodDetails());
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            img.setImageURI(imageUri);
        }
    }

    private void updateFoodDetails() {
        errorImg.setVisibility(View.GONE);
        errorTitle.setVisibility(View.GONE);
        errorDes.setVisibility(View.GONE);
        errorPrice.setVisibility(View.GONE);
        errorTime.setVisibility(View.GONE);
        errorCate.setVisibility(View.GONE);

        final String title = titleTxt.getText().toString().trim();
        final String description = desTxt.getText().toString().trim();
        final String price = priceTxt.getText().toString().trim();
        final String cate = cateTxt.getText().toString().trim();
        final String time = timeTxt.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            errorTitle.setText("Title is not null!");
            errorTitle.setVisibility(View.VISIBLE);
            return;
        }

        if (TextUtils.isEmpty(cate)) {
            errorCate.setText("Category is not null!");
            errorCate.setVisibility(View.VISIBLE);
            return;
        }

        if (TextUtils.isEmpty(description)) {
            errorDes.setText("Description is not null!");
            errorDes.setVisibility(View.VISIBLE);
            return;
        }

        if (TextUtils.isEmpty(price)) {
            errorPrice.setText("Price is not null!");
            errorPrice.setVisibility(View.VISIBLE);
            return;
        }

        if (TextUtils.isEmpty(time)) {
            errorTime.setText("Time is not null!");
            errorTime.setVisibility(View.VISIBLE);
            return;
        }

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        updateFoodInDatabase(title, description, price, time, imageUrl, Integer.parseInt(cate));
                    }))
                    .addOnFailureListener(e -> Toast.makeText(EditFoodActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            updateFoodInDatabase(title, description, price, time, currentFood.getImagePath(), Integer.parseInt(cate));
        }
    }

    private void updateFoodInDatabase(String title, String description, String price, String time, String imageUrl, int category) {
        currentFood.setTitle(title);
        currentFood.setDescription(description);
        currentFood.setPrice(Double.parseDouble(price));
        currentFood.setTimeValue(Integer.parseInt(time));
        currentFood.setImagePath(imageUrl);
        currentFood.setCategoryId(category);

        databaseReference.child(String.valueOf(currentFood.getId())).setValue(currentFood).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedFood", currentFood);
                setResult(RESULT_OK, resultIntent);
                Toast.makeText(EditFoodActivity.this, "Food updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditFoodActivity.this, "Failed to update food", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initEdit() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Foods");
        storageReference = FirebaseStorage.getInstance().getReference();
        img = findViewById(R.id.img);
        titleTxt = findViewById(R.id.titleTxt);
        desTxt = findViewById(R.id.desTxt);
        priceTxt = findViewById(R.id.priceTxt);
        timeTxt = findViewById(R.id.timeTxt);
        btUpdate = findViewById(R.id.btAdd);
        cateTxt = findViewById(R.id.cateTxt);
        Title = findViewById(R.id.Title);
        Title.setText("Edit Food");

        errorImg = findViewById(R.id.errorImg);
        errorTitle = findViewById(R.id.errorTitle);
        errorDes = findViewById(R.id.errorDes);
        errorPrice = findViewById(R.id.errorPrice);
        errorTime = findViewById(R.id.errorTime);
        errorCate = findViewById(R.id.errorCate);

        errorImg.setVisibility(View.GONE);
        errorTitle.setVisibility(View.GONE);
        errorDes.setVisibility(View.GONE);
        errorPrice.setVisibility(View.GONE);
        errorTime.setVisibility(View.GONE);
        errorCate.setVisibility(View.GONE);
    }
}
