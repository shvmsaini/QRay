package com.major.qr.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.major.qr.databinding.ProfileBinding;

public class ProfileFragment extends Fragment {
    ProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ProfileBinding.inflate(getLayoutInflater());
        binding.profileName.setText(LoginActivity.NAME);
        binding.profileEmail.setText(LoginActivity.EMAIL);
        binding.logoutButton.setOnClickListener(v -> {
            SharedPreferences preferences = requireActivity().getSharedPreferences(
                    requireActivity().getPackageName(), Context.MODE_PRIVATE);
            preferences.edit().clear().apply();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });
        return binding.getRoot();
    }
}
