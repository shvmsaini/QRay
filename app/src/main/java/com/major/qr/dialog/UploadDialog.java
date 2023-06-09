package com.major.qr.dialog;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.major.qr.R;
import com.major.qr.databinding.DialogUploadBinding;
import com.major.qr.utils.FileUtils;
import com.major.qr.viewmodels.DocumentViewModel;

import java.io.File;

public class UploadDialog extends DialogFragment {
    public static final String TAG = UploadDialog.class.getSimpleName();
    private final int FILE_PICK_REQUEST = 200;
    private final int REQUEST_CAPTURE_IMAGE = 201;
    Uri uri;
    String imageFilePath;
    private DialogUploadBinding binding;
    private String documentReference;
    private final DocumentViewModel documentViewModel;

    public UploadDialog(String documentReference, DocumentViewModel documentViewModel) {
        this.documentViewModel = documentViewModel;
        this.documentReference = documentReference;
    }

    public UploadDialog(DocumentViewModel documentViewModel) {
        this.documentViewModel = documentViewModel;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        binding = DialogUploadBinding.inflate(getLayoutInflater());
        builder.setView(binding.getRoot()).setTitle("Select File");

        binding.fileSelect.setOnClickListener(view -> {
            String[] supportedMimeTypes = {"application/pdf", "application/msword", "image/*"};
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, supportedMimeTypes);

            try {
                startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_PICK_REQUEST);
            } catch (ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(getContext(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
            }
        });

        if (documentReference != null)
            binding.docReference.setText(documentReference);

        binding.upload.setOnClickListener(view -> {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.MANAGE_EXTERNAL_STORAGE
                    }, 1
            );
            // If you have access to the external storage, do whatever you need
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // If you don't have access, launch a new activity to show the user the system's dialog
                    // to allow access to the external storage
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }

            String path = binding.fileSelect.getText().toString();
            if (!path.isEmpty() || !path.equals(getResources().getString(R.string.file_name))) {
                String[] strings = FileUtils.getPath(getContext(), uri).split("/");
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < strings.length - 1; ++i)
                    stringBuilder.append("/").append(strings[i]);
                final String filePath = stringBuilder.toString();
                final String fileName = strings[strings.length - 1];
                File file = new File(filePath, fileName);
                Log.d(TAG, "onCreate: " + file.getAbsolutePath());
                if (documentReference != null)
                    documentViewModel.updateDoc(documentReference, file).observe(this, s -> {
                        Toast.makeText(getContext(), "Successfully updated!", Toast.LENGTH_SHORT).show();
                    });
                else documentViewModel.uploadDoc(file).observe(this, s -> {
                    Toast.makeText(getContext(), "Successfully uploaded!", Toast.LENGTH_SHORT).show();
                });
            } else
                Toast.makeText(getContext(), "Select a file first!", Toast.LENGTH_SHORT).show();
        });

        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_PICK_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Get the Uri of the selected file
                uri = data.getData();
                Log.d(TAG, "File Uri: " + uri.toString());
                String path;
                String[] projection = {MediaStore.Files.FileColumns.DATA};
                Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
                if (cursor == null) {
                    path = uri.getPath();
                } else {
                    cursor.moveToFirst();
                    int column_index = cursor.getColumnIndexOrThrow(projection[0]);
                    path = cursor.getString(column_index);
                    cursor.close();
                }
                if (path == null || path.isEmpty()) {
                    Log.d("uri.getPath() = ", uri.getPath());
                    binding.fileSelect.setText(uri.getPath());
                } else {
                    Log.d("path = ", path);
                    binding.fileSelect.setText(path);
                }
            }
        }
        if (requestCode == REQUEST_CAPTURE_IMAGE) {
            Log.d(requestCode + "", resultCode + "");
            if (resultCode == RESULT_OK) {
                Toast.makeText(getContext(), "Captured! Click on Upload to upload the image.",
                        Toast.LENGTH_SHORT).show();
                binding.fileSelect.setText(imageFilePath);
                uri = Uri.fromFile(new File(imageFilePath));
                Log.d(TAG, "onActivityResult: " + uri);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}