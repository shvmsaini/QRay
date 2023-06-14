package com.major.qr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.major.qr.adapters.AttendanceDisplayAdapter;
import com.major.qr.databinding.FragmentAttendanceBinding;
import com.major.qr.utils.CaptureActivityPortrait;
import com.major.qr.viewmodels.AttendanceViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AttendanceFragment extends Fragment {
    private final static String TAG = AttendanceFragment.class.getSimpleName();
    AttendanceViewModel viewModel;
    FragmentAttendanceBinding binding;
    private AttendanceDisplayAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAttendanceBinding.inflate(getLayoutInflater());
        viewModel = ViewModelProviders.of(this).get(AttendanceViewModel.class);

        viewModel.getAttendances().observe(getViewLifecycleOwner(), attendances -> {
            adapter = new AttendanceDisplayAdapter(requireContext(), attendances);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.recyclerView.setAdapter(adapter);
        });

        binding.createAttendance.setOnClickListener(v -> {
            final String url = LoginActivity.URL + "/attendance/create";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
                Log.d(TAG, response);
                Toast.makeText(requireContext(), "Successfully created attendance register"
                        + "with registerID:" + response, Toast.LENGTH_SHORT).show();
//                Intent intent = getIntent();
//                finish();
//                startActivity(intent);
            }, error -> Log.d(TAG, "uploadDoc: No internet Connection")) {
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
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                RequestQueue requestQueue = Volley.newRequestQueue(requireActivity().getApplication());
                requestQueue.add(stringRequest);
            }
        });
        return binding.getRoot();
    }
}
