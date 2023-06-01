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
import com.androidnetworking.AndroidNetworking;
import com.major.qr.databinding.LoginLayoutBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

public class LoginActivity extends AppCompatActivity {
    public final static String URL = "http://65.2.169.108:8080/api";
    // User Details
    public static String NAME;
    public static String EMAIL;
    public static String ACCESS_TOKEN;
    public static String USERID;
    private final String TAG = getClass().getSimpleName();
    public LoginLayoutBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.enableLogging();
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
//               Intent i = new Intent(LoginActivity.this, Dashboard.class)
//                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(i);
//                if(true) return;
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
            JSONObject jsonBody = new JSONObject() {{
                try {
                    put("email", email);
                    put("password", password);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }};

            final String requestBody = jsonBody.toString();
            System.out.println(requestBody);
            Log.d(TAG, "requestBody = " + requestBody);

            final String url = URL + "/user/login";
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                Log.e("HttpClient", "success! response: " + response);
                Toast.makeText(this, "Success! Logging in...", Toast.LENGTH_SHORT)
                        .show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("uid") && !jsonObject.isNull("uid")) {
                        USERID = (String) jsonObject.get("uid");
                        NAME = (String) jsonObject.get("displayName");
                        EMAIL = (String) jsonObject.get("email");
                        ACCESS_TOKEN = (String) jsonObject.get("accessToken");
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Intent intent = new Intent(LoginActivity.this, Dashboard.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }, error -> {
                Log.e("HttpClient", "error: " + error.toString());
            }) {
                @Override
                protected Map<String, String> getParams() {
                    return new HashMap<String, String>() {{
                        put("email", email);
                        put("password", password);
                    }};
                }

                @Override
                public Map<String, String> getHeaders() {
                    return new HashMap<String, String>() {{
                        put("Content-Type", "application/json");
                        //put("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVRXp1SzRFczZhVEtsb3BUcFhMU2NjeEV3cDkzIiwiaWF0IjoxNjg1NTIxNzEzLCJleHAiOjE2ODU2MDgxMTN9.CWVzBdTWrI9vHTWJhGiK1Bfaw7PInzRIfLHjnvT1yTg");
                    }};
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    return requestBody.getBytes(StandardCharsets.UTF_8);
                }
            };
            queue.add(request);

        });
    }
}
