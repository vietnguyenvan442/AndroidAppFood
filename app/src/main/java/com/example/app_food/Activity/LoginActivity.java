package com.example.app_food.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.app_food.Domain.User;
import com.example.app_food.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends BaseActivity {
    private EditText email, password;
    private AppCompatButton login;
    private TextView signup, errorEmail, errorPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initLogin();
        signup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorEmail.setVisibility(View.GONE);
                errorPassword.setVisibility(View.GONE);
                if (email.getText().toString().isEmpty()){
                    errorEmail.setText("Email not null!");
                    errorEmail.setVisibility(View.VISIBLE);
                } else if (password.getText().toString().isEmpty()){
                    errorPassword.setText("Password not null!");
                    errorPassword.setVisibility(View.VISIBLE);
                } else if (email.getText().toString().equals("admin") && password.getText().toString().equals("admin")) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                    startActivity(intent);
                } else {
                    DatabaseReference myRef = database.getReference("Users");
                    myRef.orderByChild("email").equalTo(email.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot issue : snapshot.getChildren()) {
                                    User u = issue.getValue(User.class);
                                    if (u.getPassword().equals(password.getText().toString())) {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.putExtra("user", u);
                                        startActivity(intent);
                                        return; // Dừng việc kiểm tra khi tìm thấy người dùng
                                    }
                                }
                                // Nếu email đúng nhưng mật khẩu không khớp
                                errorPassword.setText("Password is incorrect!");
                                errorPassword.setVisibility(View.VISIBLE);
                            } else {
                                // Nếu không tìm thấy email trong cơ sở dữ liệu
                                errorEmail.setText("Email does not exist!");
                                errorEmail.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Xử lý lỗi nếu có
                        }
                    });
                }
            }
        });
    }

    private void initLogin() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.loginBtn);
        signup = findViewById(R.id.signupTxt);
        errorEmail = findViewById(R.id.errorEmail); errorEmail.setVisibility(View.GONE);
        errorPassword = findViewById(R.id.errorPassword); errorPassword.setVisibility(View.GONE);
    }
}