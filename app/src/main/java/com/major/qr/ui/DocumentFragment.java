package com.major.qr.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.divider.MaterialDividerItemDecoration;
import com.major.qr.adapters.DocDisplayAdapter;
import com.major.qr.databinding.FragmentDocumentBinding;
import com.major.qr.dialog.CreateQRDialog;
import com.major.qr.dialog.UploadDialog;
import com.major.qr.viewmodels.DocumentViewModel;
import com.major.qr.viewmodels.QrLinkViewModel;

public class DocumentFragment extends Fragment {
    private final String TAG = DocumentFragment.class.getSimpleName();
    private FragmentDocumentBinding binding;
    private DocumentViewModel documentViewModel;
    private DocDisplayAdapter displayAdapter;
    private QrLinkViewModel qrLinkViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentDocumentBinding.inflate(getLayoutInflater());

        qrLinkViewModel = new QrLinkViewModel(requireActivity().getApplication());
        documentViewModel = new DocumentViewModel(requireActivity().getApplication());

        documentViewModel.getDocs().observe(getViewLifecycleOwner(), docs -> {
            displayAdapter = new DocDisplayAdapter(getContext(), docs, documentViewModel, binding);
            binding.docRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.docRecyclerView.setAdapter(displayAdapter);
            binding.docRecyclerView.addItemDecoration(new MaterialDividerItemDecoration(requireContext(),
                    LinearLayoutManager.VERTICAL));
            binding.progressCircular.setVisibility(View.GONE);
            if (docs.size() == 0) binding.emptyView.setVisibility(View.VISIBLE);
        });

        binding.uploadDocument.setOnClickListener(view -> {
            if (displayAdapter.selectedDocs.size() > 0) {
                CreateQRDialog qrDialog = new CreateQRDialog(displayAdapter.selectedDocs, qrLinkViewModel);
                qrDialog.show(requireActivity().getSupportFragmentManager(), "Your Dialog");
            } else {
                UploadDialog uploadDialog = new UploadDialog(documentViewModel);
                uploadDialog.show(requireActivity().getSupportFragmentManager(), "Your Dialog");
            }
        });

        binding.clearAll.setOnClickListener(view -> {
            binding.docRecyclerView.setAdapter(displayAdapter);
            displayAdapter.selectedDocs.clear();
            binding.clearAll.setVisibility(View.GONE);
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        Log.d(TAG, "handleOnBackPressed() called");
                        if (displayAdapter.selectedDocs.size() > 0) {
                            binding.clearAll.performClick();
                        } else {
                            // OnBackPressed causing infinite loop
                            requireActivity().finish();
                        }
                    }
                });

        return binding.getRoot();
    }

}

