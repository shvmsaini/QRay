package com.major.qr.ui;

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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.major.qr.databinding.ActivitySignupBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class SignupActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    public ActivitySignupBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // For log in link
        SpannableString signupSpan = new SpannableString(binding.loginBack.getText().toString());
        signupSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                onBackPressed();
            }
        }, 25, 30, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        binding.loginBack.setText(signupSpan);
        binding.loginBack.setMovementMethod(LinkMovementMethod.getInstance());
        // Details
        binding.signupButton.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: Preparing Request");
            final String name = binding.nameForSignup.getText().toString();
            final String email = binding.emailForSignup.getText().toString();
            final String password = binding.passwordForSignup.getText().toString();
            final String confirmPassword = binding.confirmPasswordForSignup.getText().toString();
            final String country = binding.country.getText().toString();
            //final String collegeId = binding.collegeId.getText().toString();
            if (!validate(name, email, password, confirmPassword, country))
                return;
            Log.d(TAG, "onCreate: Preparing Request");
            // Everything works, send registration request
            RequestQueue queue = Volley.newRequestQueue(this);
            final String url = LoginActivity.URL + "/user/register";
            final JSONObject mp = new JSONObject();
            try {
                mp.put("country", "india");
                mp.put("email", email);
                mp.put("firstName", name);
                mp.put("lastName", name);
                mp.put("password", password);
                mp.put("phoneNumber", name);
                mp.put("state", "delhi");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            final String requestBody = mp.toString();
            // Request a string response from the provided URL.
            StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                // Display the first 500 characters of the response string.
                Log.d(this.getClass().getSimpleName(), "Response is: " + response);
                Toast.makeText(this, "Successfully registered!", Toast.LENGTH_SHORT).show();
                onBackPressed();
                // Toast.makeText(this, "Response is: " + response, Toast.LENGTH_SHORT).show();
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error.toString());
                    Log.d(this.getClass().getSimpleName(), "That didn't work!");
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    return requestBody.getBytes(StandardCharsets.UTF_8);
                }
            };
            Log.d(TAG, "onCreate: Sending Request: " + request);
            // Add the request to the RequestQueue.
            queue.add(request);

        });


    }

    boolean validate(String name, String email, String password, String confirmPassword,
                     String country) {
        if (name.length() == 0) {
            Toast.makeText(this, "Well, give your name please!",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (email.length() < 2) { //TODO: Email validator
            Toast.makeText(this, "Well, give your email please!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Well, password length should be at least 6!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!confirmPassword.equals(password)) {
            Toast.makeText(this, "Well, both password should match!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (country.length() == 0) {
            Toast.makeText(this, "Well, mention you country please!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }
}
