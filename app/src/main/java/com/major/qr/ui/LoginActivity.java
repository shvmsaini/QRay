package com.major.qr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.major.qr.databinding.ActivityLoginBinding;
import com.major.qr.viewmodels.LoginViewModel;

import org.json.JSONException;

public class LoginActivity extends AppCompatActivity {
    public final static String URL = "http://43.205.195.167:8080/api";
    public static String NAME;
    public static String EMAIL;
    public static String ACCESS_TOKEN;
    public static String USERID;
    private final String TAG = getClass().getSimpleName();
    public ActivityLoginBinding binding;
    public LoginViewModel loginViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.enableLogging();

        loginViewModel = new LoginViewModel(getApplication());

        // For sign up link
        SpannableString signupSpan = new SpannableString(binding.signup.getText().toString());
        signupSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent signupIntent = new Intent(LoginActivity.this,
                        SignupActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(signupIntent);
            }
        }, 23, 29, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        binding.signup.setText(signupSpan);
        binding.signup.setMovementMethod(LinkMovementMethod.getInstance());

        // Login
        binding.loginButton.setOnClickListener(view -> {
//             TODO: Get and Store USERID in preferences
            String email = binding.emailForLogin.getText().toString().trim();
            String password = binding.passwordForLogin.getText().toString();
            if (email.length() == 0) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() == 0) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            loginViewModel.getLoginData(email, password).observe(this, jsonObject -> {
                try {
                    if (jsonObject.has("uid") && !jsonObject.isNull("uid")) {
                        USERID = (String) jsonObject.get("uid");
                        NAME = (String) jsonObject.get("displayName");
                        EMAIL = (String) jsonObject.get("email");
                        ACCESS_TOKEN = (String) jsonObject.get("accessToken");
                    }
                } catch (JSONException e) {
                    Toast.makeText(this, "Unable to login!", Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }
                Intent intent = new Intent(this, Dashboard.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(getApplication(), "Success! Logging in...", Toast.LENGTH_SHORT)
                        .show();
            });

        });
    }
}
