package com.major.qr.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.major.qr.R;
import com.major.qr.adapters.AttendanceDisplayAdapter;
import com.major.qr.databinding.FragmentAttendanceBinding;
import com.major.qr.viewmodels.AttendanceViewModel;

public class AttendanceFragment extends Fragment {
    private final String TAG = AttendanceFragment.class.getSimpleName();
    AttendanceViewModel viewModel;
    FragmentAttendanceBinding binding;
    private AttendanceDisplayAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAttendanceBinding.inflate(getLayoutInflater());
        viewModel = ViewModelProviders.of(this).get(AttendanceViewModel.class);

        viewModel.getAttendances().observe(getViewLifecycleOwner(), attendances -> {
            adapter = new AttendanceDisplayAdapter(requireContext(), attendances, viewModel);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.recyclerView.setAdapter(adapter);
        });

        binding.createAttendance.setOnClickListener(v -> viewModel.createAttendance().observe(getViewLifecycleOwner(), jsonObject -> {
            if (jsonObject != null) {
                Toast.makeText(requireContext(), "Successfully created attendance register.",
                        Toast.LENGTH_SHORT).show();
                adapter.notifyItemInserted(adapter.getItemCount());
                // Restart Fragment
                getParentFragmentManager().beginTransaction().replace(R.id.fragment,
                        new AttendanceFragment()).commit();
            } else {
                Toast.makeText(requireContext(), "Unable to create attendance register.",
                        Toast.LENGTH_SHORT).show();
            }
        }));
        return binding.getRoot();
    }
}
