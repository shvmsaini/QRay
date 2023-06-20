package com.major.qr.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.major.qr.databinding.ActivityLoginBinding;
import com.major.qr.viewmodels.LoginViewModel;

import org.json.JSONException;

public class LoginActivity extends AppCompatActivity {
    public final static String URL = "http://43.205.215.214:8080/api";
    public static String NAME;
    public static String EMAIL;
    public static String ACCESS_TOKEN;
    public static String USERID;
    public static RequestQueue requestQueue;
    private final String TAG = getClass().getSimpleName();
    public ActivityLoginBinding binding;
    public LoginViewModel loginViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Volley
        requestQueue = Volley.newRequestQueue(this.getApplication());

        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.enableLogging();

        loginViewModel = new LoginViewModel(getApplication());
        SharedPreferences preferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        // For signup activity link
        SpannableString signupSpan = new SpannableString(binding.signup.getText().toString());
        signupSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent signupIntent = new Intent(LoginActivity.this, SignupActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(signupIntent);
            }
        }, 23, 29, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        binding.signup.setText(signupSpan);
        binding.signup.setMovementMethod(LinkMovementMethod.getInstance());

        binding.loginButton.setOnClickListener(view -> {
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

            SharedPreferences.Editor editor = preferences.edit();
            loginViewModel.getLoginData(email, password).observe(this, jsonObject -> {
                if (jsonObject == null) {
                    Toast.makeText(this, "Unable to login!", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (jsonObject.has("uid") && !jsonObject.isNull("uid")) {
                        USERID = (String) jsonObject.get("uid");
                        NAME = (String) jsonObject.get("displayName");
                        EMAIL = (String) jsonObject.get("email");
                        ACCESS_TOKEN = (String) jsonObject.get("accessToken");
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                editor.putString("uid", USERID);
                editor.putString("access_token", ACCESS_TOKEN);
                editor.putString("name", NAME);
                editor.putString("email", EMAIL);
                editor.apply();
                Intent intent = new Intent(this, DashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                Toast.makeText(getApplication(), "Success! Logging in...", Toast.LENGTH_SHORT).show();
            });

        });

        // Checking if user is already logged with stored info
        if (!preferences.getString("uid", "none").equals("none")) {
            USERID = preferences.getString("uid", "");
            ACCESS_TOKEN = preferences.getString("access_token", "");
            NAME = preferences.getString("name", "");
            EMAIL = preferences.getString("email", "");
            Log.d(TAG, "ACCESS_TOKEN = " + ACCESS_TOKEN);
            Log.d(TAG, "USERID = " + USERID);
            Log.d(TAG, "NAME = " + NAME);
            Log.d(TAG, "EMAIL = " + EMAIL);
            Intent intent = new Intent(this, DashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
