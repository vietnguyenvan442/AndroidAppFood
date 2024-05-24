package com.example.app_food.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.app_food.Activity.EditFoodActivity;
import com.example.app_food.Adapter.ListFoodAdapterAdmin;
import com.example.app_food.Adapter.OnItemClickListener;
import com.example.app_food.Domain.Category;
import com.example.app_food.Domain.Foods;
import com.example.app_food.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListFoodFragment extends Fragment {
    private List<Foods> list;
    private ListFoodAdapterAdmin adapter;
    private RecyclerView recyclerView;
    private EditText searchView;
    private FirebaseDatabase database;
    private ProgressBar progressBar;
    private Spinner spinner;
    private ImageView btSearch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_food, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData(view);
        setListFood();
    }

    private void setListFood() {
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseReference myRef = database.getReference("Foods");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    list.add(dataSnapshot.getValue(Foods.class));
                }
                adapter = new ListFoodAdapterAdmin(list);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = (String) parent.getItemAtPosition(position);
                list.clear(); // Xóa danh sách trước khi thêm dữ liệu mới
                progressBar.setVisibility(View.VISIBLE);
                if (position == 0) {
                    DatabaseReference myRef = database.getReference("Foods");
                    Query query = myRef.orderByChild("Name");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot issue : snapshot.getChildren()) {
                                    list.add(issue.getValue(Foods.class));
                                }
                                if (!list.isEmpty()) {
                                    adapter = new ListFoodAdapterAdmin(list);
                                    recyclerView.setAdapter(adapter);
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    DatabaseReference catRef = database.getReference("Category");
                    Query query = catRef.orderByChild("Name").equalTo(category);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int cate_id = -1;
                            if (snapshot.exists()) {
                                for (DataSnapshot issue : snapshot.getChildren()) {
                                    cate_id = issue.getValue(Category.class).getId();
                                    break;
                                }
                            }

                            if (cate_id != -1) {
                                DatabaseReference myRef = database.getReference("Foods");
                                Query query = myRef.orderByChild("CategoryId").equalTo(cate_id);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot issue : snapshot.getChildren()) {
                                                list.add(issue.getValue(Foods.class));
                                            }
                                            if (!list.isEmpty()) {
                                                adapter = new ListFoodAdapterAdmin(list);
                                                recyclerView.setAdapter(adapter);
                                            }
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTxt = searchView.getText().toString();
                if (searchTxt != null) {
                    ArrayList<Foods> convertList = new ArrayList<>();
                    for (Foods food : list) {
                        if (food.getTitle().toLowerCase().contains(searchTxt.toLowerCase())) {
                            convertList.add(food);
                        }
                    }
                    adapter = new ListFoodAdapterAdmin(convertList);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Foods updatedFood = (Foods) data.getSerializableExtra("updatedFood");
            if (updatedFood != null) {
                updateFoodInList(updatedFood);
            }
        }
    }

    private void updateFoodInList(Foods updatedFood) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == updatedFood.getId()) {
                list.set(i, updatedFood);
                adapter.notifyItemChanged(i);
                break;
            }
        }
    }


    private void initData(View view) {
        database = FirebaseDatabase.getInstance();
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.foodListView);
        searchView = view.findViewById(R.id.search);
        spinner = view.findViewById(R.id.spinner);
        btSearch = view.findViewById(R.id.btSearch);
    }


}