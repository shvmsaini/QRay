package com.major.qr.ui;

import android.content.Intent;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.major.qr.databinding.LoginactivityBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    public LoginactivityBinding binding;
    private final String TAG = getClass().getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // For sign up link
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

        // Login
        binding.loginButton.setOnClickListener(view -> {
               Intent i = new Intent(LoginActivity.this, Dashboard.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return;
//            String email = binding.emailForLogin.getText().toString().trim();
//            String password = binding.passwordForLogin.getText().toString();
//            if (email.length() == 0) {
//                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if (password.length() == 0) {
//                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            final String url = "http://192.168.0.197:8080/api/user/login?email=" + email + "&password=" + password;
//            RequestQueue queue = Volley.newRequestQueue(this);
//            StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
//                Log.e("HttpClient", "success! response: " + response);
//                Toast.makeText(this, "Success! Logging in...", Toast.LENGTH_SHORT).show();
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    if (jsonObject.has("uid") && !jsonObject.isNull("uid")) {
//                        String UID = (String) jsonObject.get("uid");
//                        Log.d(TAG, "onCreate: " + UID);
//                    }
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//                Intent i = new Intent(LoginActivity.this, Dashboard.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(i);
//            }, error -> {
//                Log.e("HttpClient", "error: " + error.toString());
//            }) {
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("email", email);
//                    params.put("password", password);
//                    Log.d(TAG, "getParams: " + params);
//                    return params;
//                }
//
//                @Override
//                public Map<String, String> getHeaders() {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("Content-Type", "application/x-www-form-urlencoded");
//                    return params;
//                }
//            };
//            queue.add(request);

        });
    }
}
