package com.major.qr.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.major.qr.adapters.AttendanceDisplayAdapter;
import com.major.qr.databinding.FragmentAttendanceBinding;
import com.major.qr.dialog.AttendanceNameDialog;
import com.major.qr.dialog.UploadDialog;
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
        viewModel = new ViewModelProvider(requireActivity()).get(AttendanceViewModel.class);

        viewModel.getAttendances().observe(getViewLifecycleOwner(), attendances -> {
            adapter = new AttendanceDisplayAdapter(requireContext(), attendances, viewModel);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.progressCircular.setVisibility(View.GONE);
            if (attendances.size() == 0) binding.emptyView.setVisibility(View.VISIBLE);
            else binding.emptyView.setVisibility(View.GONE);
            binding.recyclerView.setAdapter(adapter);
        });

        binding.createAttendance.setOnClickListener(view -> {
            AttendanceNameDialog nameDialog = new AttendanceNameDialog(viewModel, adapter);
            nameDialog.show(requireActivity().getSupportFragmentManager(), "Attendance Name Dialog");
        });

        return binding.getRoot();
    }
}
