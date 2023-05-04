package com.major.qr.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.major.qr.databinding.ProfileBinding;

public class ProfileFragment extends Fragment {
    private ProfileBinding binding;
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
        // view = inflater.inflate(binding.getRoot(), container, false);
    }
}
