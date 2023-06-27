package com.major.qr.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.major.qr.R;
import com.major.qr.databinding.FragmentProfileBinding;
import com.major.qr.models.User;
import com.major.qr.viewmodels.ProfileViewModel;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    ProfileViewModel viewModel;
    boolean editMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());

        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        viewModel.getUserDetails().observe(getViewLifecycleOwner(), user -> {
            binding.firstName.setText(user.getFirstName());
            binding.lastName.setText(user.getLastName());
            binding.profileEmail.setText(user.getEmail());
            binding.country.setText(user.getCountry());
            binding.phoneNumber.setText(user.getPhoneNumber());
            final String country = user.getCountry().toLowerCase();
            switch (country) {
                case "india":
                    binding.countryFlag.setText("🇮🇳");
                    break;
                case "america":
                    binding.countryFlag.setText("🇺🇸");
                    break;
                case "canada":
                    binding.countryFlag.setText("🇨🇦");
                    break;
                case "pakistan":
                    binding.countryFlag.setText("🇵🇰");
                    break;
                default:
                    binding.countryFlag.setText("🏳");
            }
        });

        ArrayList<EditText> list = new ArrayList<EditText>() {{
            add(binding.firstName);
            add(binding.lastName);
            add(binding.profileEmail);
            add(binding.country);
            add(binding.phoneNumber);
        }};

        viewMode(list);

        binding.editProfileButton.setOnClickListener(view -> {
            if (editMode) {
                User user = new User();
                user.setFirstName(binding.firstName.getText().toString());
                user.setLastName(binding.lastName.getText().toString());
                user.setEmail(binding.profileEmail.getText().toString());
                user.setCountry(binding.country.getText().toString());
                user.setPhoneNumber(binding.phoneNumber.getText().toString());
                user.setState("state");
                viewModel.updateUserDetails(user).observe(getViewLifecycleOwner(), response -> {
                    viewMode(list);
                    if (response == null) {
                        Toast.makeText(requireContext(), "Failed to update.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(requireContext(), "Successfully updated!", Toast.LENGTH_SHORT).show();
                });
                return;
            }
            editMode(list);
        });

        binding.logoutButton.setOnClickListener(v -> {
            if (editMode) {
                viewMode(list);
                return;
            }
            SharedPreferences preferences = requireActivity().getSharedPreferences(
                    requireActivity().getPackageName(), Context.MODE_PRIVATE);
            preferences.edit().clear().apply();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        return binding.getRoot();
    }

    private void viewMode(ArrayList<EditText> list) {
        editMode = false;
        binding.editProfileButton.setText("Edit Profile");
        binding.logoutButton.setText("Logout");
        binding.spacer.setVisibility(View.GONE);
        for (EditText editText : list) {
            editText.setEnabled(false);
            editText.setBackgroundResource(android.R.color.transparent);
        }
    }

    private void editMode(ArrayList<EditText> list) {
        editMode = true;
        binding.editProfileButton.setText("Save");
        binding.logoutButton.setText("Cancel");
        binding.spacer.setVisibility(View.VISIBLE);
        for (EditText editText : list) {
            editText.setEnabled(true);
            editText.setBackgroundResource(R.drawable.input_background);
            editText.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.secondary_blue)));
        }
    }
}
