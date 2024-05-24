package com.example.app_food.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.app_food.Fragment.ListFoodFragment;
import com.example.app_food.Fragment.ListUserFragment;
import com.example.app_food.Fragment.StatisticFragment;
import com.example.app_food.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity2 extends BaseActivity {
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initMain();
    }

    private void initMain() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        addButton = findViewById(R.id.add_button);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();
                if(itemId == R.id.ic_home) selectedFragment = new ListFoodFragment();
                else if (itemId == R.id.ic_list) selectedFragment = new ListUserFragment();
                else if (itemId == R.id.ic_stat) selectedFragment = new StatisticFragment();

                if (selectedFragment != null)
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                return true;
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new ListFoodFragment()).commit();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, AddFoodActivity.class);
                startActivity(intent);
            }
        });
    }
}
