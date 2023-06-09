package com.major.qr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.major.qr.databinding.ActivityAttendanceBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AttendanceActivity extends AppCompatActivity {
    private final static String TAG = AttendanceActivity.class.getSimpleName();
    String AttendanceId;
    private ActivityAttendanceBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.create.setOnClickListener(v -> {
            Intent intent = new Intent(AttendanceActivity.this, CreateAttendanceActivity.class);
            startActivity(intent);
        });

        binding.mark.setOnClickListener(v -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(this);
            intentIntegrator.setPrompt("Scan QR for marking attendance");
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.initiateScan();
            if (AttendanceId == null) return;
            Log.d(TAG, "AttendanceId: " + AttendanceId);
            JSONObject params = new JSONObject() {{
                try {
                    put("attendanceId", AttendanceId);
                    put("attendersId", LoginActivity.USERID);
                    put("displayName", LoginActivity.NAME);
                    put("email", LoginActivity.EMAIL);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }};
            final String url = LoginActivity.URL + "/attendance/mark";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params, response -> {
                Toast.makeText(this, "Marked Successfully", Toast.LENGTH_LONG).show();
                Log.d(TAG, response.toString());
            }, error -> {
                Log.d(TAG, "uploadDoc: No internet Connection");
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return new HashMap<String, String>() {{
                        put("accept", "*/*");
                        put("Authorization", LoginActivity.ACCESS_TOKEN);
                        put("Content-Type", "application/json");
                    }};
                }
            };
            {
                int socketTimeout = 30000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                RequestQueue requestQueue = Volley.newRequestQueue(this.getApplication());
                requestQueue.add(jsonObjectRequest);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                AttendanceId = intentResult.getContents();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
