package com.example.app_food.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_food.Domain.User;
import com.example.app_food.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.atomic.AtomicInteger;

public class SignupActivity extends BaseActivity {
    EditText name, email, password, confirm;
    TextView errName, errEmail, errPass, errConfirm, login;
    AppCompatButton signUp;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        initSignUp();
        signUpOnClick();

        login.setOnClickListener(v -> startActivity(new Intent(SignupActivity.this, LoginActivity.class)));
    }

    private void signUpOnClick() {
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errName.setVisibility(View.GONE);
                errEmail.setVisibility(View.GONE);
                errPass.setVisibility(View.GONE);
                errConfirm.setVisibility(View.GONE);

                final String inputName = name.getText().toString().trim();
                final String inputEmail = email.getText().toString().trim();
                final String inputPassword = password.getText().toString().trim();
                String inputConfirm = confirm.getText().toString().trim();

                if (TextUtils.isEmpty(inputName)) {
                    errName.setText("Name is not null!");
                    errName.setVisibility(View.VISIBLE);
                    return;
                }

                if (TextUtils.isEmpty(inputEmail)) {
                    errEmail.setText("Email is not null!");
                    errEmail.setVisibility(View.VISIBLE);
                    return;
                }

                if (!inputEmail.contains("@gmail.com")) {
                    errEmail.setText("Email must contain: @gmail.com");
                    errEmail.setVisibility(View.VISIBLE);
                    return;
                }

                if (TextUtils.isEmpty(inputPassword)) {
                    errPass.setText("Pass is not null!");
                    errPass.setVisibility(View.VISIBLE);
                    return;
                }

                if (TextUtils.isEmpty(inputConfirm)) {
                    errConfirm.setText("Confirm is not null!");
                    errConfirm.setVisibility(View.VISIBLE);
                    return;
                }

                if (!inputConfirm.equals(inputPassword)) {
                    errConfirm.setText("Confirm Password is not the same as Password!");
                    errConfirm.setVisibility(View.VISIBLE);
                    return;
                }

                // Kiểm tra xem email đã tồn tại trong database chưa
                Query query = databaseReference.orderByChild("email").equalTo(inputEmail);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Email đã tồn tại
                            errEmail.setText("Email Available!");
                            errEmail.setVisibility(View.VISIBLE);
                        } else {
                            // Email chưa tồn tại, thêm user mới vào database
                            User user = new User();
                            user.setEmail(inputEmail);
                            user.setPassword(inputPassword);
                            user.setName(inputName);
                            databaseReference.orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int num = (int) dataSnapshot.getChildrenCount();
                                    user.setId(num + 1);

                                    String userId = user.getId() - 1 + "";
                                    databaseReference.child(userId).setValue(user, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if (databaseError != null) {
                                                errName.setText("Sign Up Failed!");
                                                errName.setVisibility(View.VISIBLE);
                                            } else {
                                                // Thành công, chuyển tiếp đến MainActivity
                                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                intent.putExtra("user", user);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(SignupActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initSignUp() {
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.confirmPass);
        errName = findViewById(R.id.errorName);
        errEmail = findViewById(R.id.errorEmail);
        errPass = findViewById(R.id.errorPassword);
        errConfirm = findViewById(R.id.errorConfirm);
        login = findViewById(R.id.loginTxt);
        signUp = findViewById(R.id.signUpBtn);


        errName.setVisibility(View.GONE);
        errEmail.setVisibility(View.GONE);
        errPass.setVisibility(View.GONE);
        errConfirm.setVisibility(View.GONE);
    }
}
