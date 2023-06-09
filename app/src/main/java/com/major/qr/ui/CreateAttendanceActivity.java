package com.major.qr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.major.qr.adapters.AttendanceDisplayAdapter;
import com.major.qr.databinding.ActivityCreateAttendanceBinding;
import com.major.qr.viewmodels.CreateAttendanceViewModel;

import java.util.HashMap;
import java.util.Map;

public class CreateAttendanceActivity extends AppCompatActivity {
    private final String TAG = CreateAttendanceActivity.class.getSimpleName();
    private ActivityCreateAttendanceBinding binding;
    private CreateAttendanceViewModel viewModel;
    private AttendanceDisplayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = ViewModelProviders.of(this).get(CreateAttendanceViewModel.class);
        viewModel.getAttendances().observe(this, attendances -> {
            adapter = new AttendanceDisplayAdapter(this, attendances);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerView.setAdapter(adapter);
        });

        // FAB
        binding.createAttendance.setOnClickListener(v -> {
            final String url = LoginActivity.URL + "/attendance/create";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
                Log.d(TAG, response);
                Toast.makeText(this, "Successfully created attendance register"
                        + "with registerID:" + response, Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
//                viewModel.getAttendances();
            }, error -> {
                Log.d(TAG, "uploadDoc: No internet Connection");
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    return new HashMap<String, String>() {{
                        put("accept", "*/*");
                        put("Authorization", LoginActivity.ACCESS_TOKEN);
                    }};
                }
            };
            {
                int socketTimeout = 30000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                RequestQueue requestQueue = Volley.newRequestQueue(this.getApplication());
                requestQueue.add(stringRequest);
            }
        });
    }
}