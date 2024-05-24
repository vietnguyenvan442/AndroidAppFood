package com.example.app_food.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.app_food.Adapter.ListFoodAdapterAdmin;
import com.example.app_food.Adapter.ListUserAdapterAdmin;
import com.example.app_food.Domain.Foods;
import com.example.app_food.Domain.User;
import com.example.app_food.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListUserFragment extends Fragment {
    List<User> list;
    ListUserAdapterAdmin adapter;
    private RecyclerView recyclerView;
    private EditText searchView;
    private FirebaseDatabase database;
    private ProgressBar progressBar;
    private ImageView btSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData(view);
        setListUser();
    }

    private void setListUser() {
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference myRef = database.getReference("Users");
        Query query = myRef.orderByChild("name");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(User.class));
                    }
                    if (!list.isEmpty()) {
                        adapter = new ListUserAdapterAdmin(list);
                        recyclerView.setAdapter(adapter);
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTxt = searchView.getText().toString();
                if (searchTxt != null){
                    list.clear();
                    DatabaseReference myRef = database.getReference("Users");
                    Query query = myRef.orderByChild("name");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot issue : snapshot.getChildren()) {
                                    User user = issue.getValue(User.class);
                                    String title = user.getName().toLowerCase(Locale.getDefault());
                                    // Kiểm tra xem tiêu đề chứa chuỗi tìm kiếm không
                                    if (title.contains(searchTxt)) {
                                        list.add(user);
                                    }
                                }
                                if (!list.isEmpty()) {
                                    adapter = new ListUserAdapterAdmin(list);
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
        });
    }

    private void initData(View view) {
        database = FirebaseDatabase.getInstance();
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.userListView);
        searchView = view.findViewById(R.id.search);
        btSearch = view.findViewById(R.id.btSearch);
    }
}