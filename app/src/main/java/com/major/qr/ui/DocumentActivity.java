package com.major.qr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.major.qr.databinding.DocumentactivityBinding;

public class DocumentActivity extends AppCompatActivity {
    private DocumentactivityBinding binding;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DocumentactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    }
}
