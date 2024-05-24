package com.example.app_food.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.app_food.Domain.Foods;
import com.example.app_food.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddFoodActivity extends BaseActivity {
    private ImageView img;
    private EditText titleTxt, desTxt, priceTxt, timeTxt, cateTxt;
    private TextView errorImg, errorTitle, errorDes, errorPrice, errorTime, errorCate;
    private AppCompatButton btAdd;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int PICK_IMAGE = 1;
    private Uri imageUri;
    private static final int MAX_RETRIES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        initEdit();

        img.setOnClickListener(v -> openGallery());
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                if (imageUri == null) {
                    errorImg.setText("Please select an image!");
                    errorImg.setVisibility(View.VISIBLE);
                    return;
                }

                // Upload image to Firebase Storage
                uploadImageWithRetry(0);
            }
        });
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

    private void uploadImageWithRetry(int retryCount) {
        if (retryCount >= MAX_RETRIES) {
            Toast.makeText(AddFoodActivity.this, "Failed to upload image after " + MAX_RETRIES + " attempts", Toast.LENGTH_SHORT).show();
            return;
        }

        final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");

        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // Compress image to reduce size
            byte[] data = baos.toByteArray();

            fileReference.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    getMaxFoodIdAndSaveFood(titleTxt.getText().toString().trim(), desTxt.getText().toString().trim(), priceTxt.getText().toString().trim(), timeTxt.getText().toString().trim(), imageUrl, cateTxt.getText().toString().trim());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof IOException) {
                                Toast.makeText(AddFoodActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddFoodActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            uploadImageWithRetry(retryCount + 1);
                        }
                    });

        } catch (IOException e) {
            Toast.makeText(this, "Error reading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            uploadImageWithRetry(retryCount + 1);
        }
    }

    private void getMaxFoodIdAndSaveFood(String title, String description, String price, String time, String imageUrl, String category) {
        Query query = databaseReference.orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int maxId = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    maxId = Integer.parseInt(snapshot.getKey());
                }
                int newFoodId = maxId + 1;
                saveFoodDetails(newFoodId, title, description, price, time, imageUrl, category);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddFoodActivity.this, "Failed to get max food id: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveFoodDetails(int foodId, String title, String description, String price, String time, String imageUrl, String category) {
        Foods food = new Foods(foodId, description, false, foodId, 1, Double.parseDouble(price), imageUrl, 1, 0, 1, Integer.parseInt(time), title);

        databaseReference.child(String.valueOf(foodId)).setValue(food).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AddFoodActivity.this, "Food added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(AddFoodActivity.this, "Failed to add food", Toast.LENGTH_SHORT).show();
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
        btAdd = findViewById(R.id.btAdd);
        cateTxt = findViewById(R.id.cateTxt);

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
