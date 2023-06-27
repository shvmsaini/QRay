package com.major.qr.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.major.qr.R;
import com.major.qr.adapters.DocDisplayAdapter;
import com.major.qr.databinding.DialogDocumentDetailsBinding;
import com.major.qr.models.Doc;
import com.major.qr.viewmodels.DocumentViewModel;

public class DocumentDetailDialog extends DialogFragment {

    DialogDocumentDetailsBinding binding;
    DocumentViewModel viewModel;
    Doc doc;
    int position;
    DocDisplayAdapter adapter;

    public DocumentDetailDialog(DocumentViewModel viewModel, Doc doc, DocDisplayAdapter adapter, int position) {
        this.viewModel = viewModel;
        this.doc = doc;
        this.adapter = adapter;
        this.position = position;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        binding = DialogDocumentDetailsBinding.inflate(getLayoutInflater());
        builder.setView(binding.getRoot());

        binding.docType.setText(doc.getDocumentType());
        binding.docId.setText(doc.getDocumentId());
        binding.docRef.setText(doc.getDocumentReference());

        if (doc.getDocumentType().equals("pdf"))
            binding.imageView.setImageDrawable(
                    AppCompatResources.getDrawable(requireContext(), R.drawable.pdf_placeholder));
        else
            Glide.with(requireContext())
                    .load(doc.getDocLink())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.drawable.placeholder_doc)
                    .into(binding.imageView);

        binding.updateButton.setOnClickListener(view -> {
            UploadDialog uploadDialog = new UploadDialog(doc.getDocumentReference(), viewModel);
            uploadDialog.show(requireActivity().getSupportFragmentManager(), "Your Dialog");
        });

        binding.downloadButton.setOnClickListener(view -> viewModel.getDocLink(doc.getDocumentReference())
                .observe((LifecycleOwner) requireContext(), s -> {
//                    Intent i = new Intent(context, WebViewActivity.class);
//                    i.putExtra("URL", URL);
//                    context.startActivity(i);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(doc.getDocLink()));
                    requireContext().startActivity(browserIntent);
                }));


        binding.deleteButton.setOnClickListener(view -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        viewModel.deleteDoc(doc.getDocumentId(), doc.getDocumentReference())
                                .observe((LifecycleOwner) requireContext(), s -> {
                                    Toast.makeText(requireContext(), "Deleted successfully!",
                                            Toast.LENGTH_SHORT).show();
                                    adapter.list.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    dismiss();
                                });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                }
            };
            new AlertDialog.Builder(requireContext()).setMessage("Are you sure?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        });

        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }
}
