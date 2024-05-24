package com.example.app_food.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.app_food.Adapter.ListFoodAdapter;
import com.example.app_food.Domain.Foods;
import com.example.app_food.databinding.ActivityListFoodBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class ListFoodActivity extends BaseActivity {
    private ActivityListFoodBinding binding;
    private RecyclerView.Adapter adapter;
    private int categoryId, viewAll;
    private String categoryName;
    private String searchText;
    private boolean isSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        initList();
    }

    private void getIntentExtra() {
        categoryId = getIntent().getIntExtra("CategoryId", 0);
        categoryName = getIntent().getStringExtra("CategoryName");
        searchText = getIntent().getStringExtra("text");
        isSearch = getIntent().getBooleanExtra("isSearch", false);
        viewAll = getIntent().getIntExtra("viewAll", 0);

        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void initList(){
        DatabaseReference myRef = database.getReference("Foods");
        binding.progressBar.setVisibility(View.VISIBLE);
        ArrayList<Foods> list = new ArrayList<>();
        Query query;
        if (isSearch) {
            binding.titleTxt.setText("Name: " + searchText);

            String searchKey = searchText.toLowerCase(Locale.getDefault());
            query = myRef.orderByChild("Title");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            Foods food = issue.getValue(Foods.class);
                            String title = food.getTitle().toLowerCase(Locale.getDefault());
                            // Kiểm tra xem tiêu đề chứa chuỗi tìm kiếm không
                            if (title.contains(searchKey)) {
                                list.add(food);
                            }
                        }
                        if (!list.isEmpty()) {
                            binding.foodListView.setLayoutManager(new GridLayoutManager(ListFoodActivity.this, 2));
                            adapter = new ListFoodAdapter(list);
                            binding.foodListView.setAdapter(adapter);
                        }
                        binding.progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý khi tìm kiếm bị hủy
                }
            });
        } else if (viewAll == 1) {
            binding.titleTxt.setText("All Foods");
            query = myRef.orderByChild("Name");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot issue: snapshot.getChildren()){
                            list.add(issue.getValue(Foods.class));
                        }
                        if (!list.isEmpty()) {
                            binding.foodListView.setLayoutManager(new GridLayoutManager(ListFoodActivity.this, 2));
                            adapter = new ListFoodAdapter(list);
                            binding.foodListView.setAdapter(adapter);
                        }
                        binding.progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            binding.titleTxt.setText("Category: " + categoryName);
            query = myRef.orderByChild("CategoryId").equalTo(categoryId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            list.add(issue.getValue(Foods.class));
                        }
                        if (!list.isEmpty()) {
                            binding.foodListView.setLayoutManager(new GridLayoutManager(ListFoodActivity.this, 2));
                            adapter = new ListFoodAdapter(list);
                            binding.foodListView.setAdapter(adapter);
                        }
                        binding.progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

}