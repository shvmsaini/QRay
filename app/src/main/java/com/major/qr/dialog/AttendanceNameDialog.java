package com.major.qr.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;

import com.major.qr.R;
import com.major.qr.adapters.AttendanceDisplayAdapter;
import com.major.qr.databinding.DialogAttendanceNameBinding;
import com.major.qr.ui.AttendanceFragment;
import com.major.qr.viewmodels.AttendanceViewModel;

public class AttendanceNameDialog extends DialogFragment {
    AttendanceViewModel viewModel;
    AttendanceDisplayAdapter adapter;
    DialogAttendanceNameBinding binding;

    public AttendanceNameDialog(AttendanceViewModel viewModel, AttendanceDisplayAdapter adapter) {
        this.viewModel = viewModel;
        this.adapter = adapter;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        binding = DialogAttendanceNameBinding.inflate(getLayoutInflater());
        builder.setView(binding.getRoot());

        binding.create.setOnClickListener(view -> {
            final String name = binding.name.getText().toString();
            if (name.length() == 0) {
                Toast.makeText(requireContext(), "Enter attendance name", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.createAttendance(name).observe((LifecycleOwner) requireContext(), jsonObject -> {
                if (jsonObject != null) {
                    Toast.makeText(requireContext(), "Successfully created attendance register.",
                            Toast.LENGTH_SHORT).show();
                    adapter.notifyItemInserted(adapter.getItemCount());
                    // Restart Fragment
                    getParentFragmentManager().beginTransaction().replace(R.id.fragment,
                            new AttendanceFragment()).commit();
                    dismiss();
                } else {
                    Toast.makeText(requireContext(), "Unable to create attendance register.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }
}
