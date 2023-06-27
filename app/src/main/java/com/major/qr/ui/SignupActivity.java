package com.major.qr.ui;

import static com.major.qr.ui.LoginActivity.requestQueue;

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

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.major.qr.databinding.ActivitySignupBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private final String TAG = getClass().getSimpleName();
    private ActivitySignupBinding binding;

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
            Log.d(TAG, "Preparing Request");
            final String name = binding.nameForSignup.getText().toString();
            final String lastName = binding.lastNameForSignup.getText().toString();
            final String email = binding.emailForSignup.getText().toString();
            final String password = binding.passwordForSignup.getText().toString();
            final String confirmPassword = binding.confirmPasswordForSignup.getText().toString();
            final String phoneNumber = binding.phoneNumber.getText().toString();
            final String country = binding.country.getText().toString();
            if (!validate(name, email, password, confirmPassword, phoneNumber, country))
                return;
            final String url = LoginActivity.URL + "/user/register";
            final JSONObject mp = new JSONObject() {{
                try {
                    put("country", country);
                    put("email", email);
                    put("firstName", name);
                    put("lastName", lastName);
                    put("password", password);
                    put("phoneNumber", phoneNumber);
                    put("state", "delhi");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }};

            Log.d(TAG, "Request body" + Arrays.toString(mp.toString().getBytes(StandardCharsets.UTF_8)));

            StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                Log.d(TAG, "Response is: " + response);
                Toast.makeText(this, "Successfully registered!", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }, error -> {
                Toast.makeText(this, "Unable to register! Make sure you are entering" +
                        " an email address that is not already registered.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onErrorResponse: " + error.toString());
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    return new HashMap<String, String>() {{
                        put("accept", "*/*");
                        put("Content-Type", "application/json");
                    }};
                }

                @Override
                public byte[] getBody() {
                    return mp.toString().getBytes(StandardCharsets.UTF_8);
                }
            };
            requestQueue.add(request);
        });
    }

    boolean validate(String name, String email, String password, String confirmPassword, String phoneNumber,
                     String country) {
        if (name.length() == 0) {
            Toast.makeText(this, "Please enter your name.",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (!validateEmail(email)) {
            Toast.makeText(this, "Please Enter valid email address.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phoneNumber.length() != 10) {
            Toast.makeText(this, "Please Enter valid phone number.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password length should be at least 6!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!confirmPassword.equals(password)) {
            Toast.makeText(this, "Password didn't match!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (country.length() == 0) {
            Toast.makeText(this, "Please enter you country.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateEmail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.matches();
    }
}
