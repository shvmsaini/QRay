package com.major.qr.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.major.qr.databinding.AttendanceactivityBinding;
import com.major.qr.databinding.DocumentactivityBinding;

public class AttendanceActivity extends AppCompatActivity {
    private AttendanceactivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AttendanceactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
