package com.major.qr.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.major.qr.adapters.DocDisplayAdapter;
import com.major.qr.databinding.ActivityDocumentBinding;
import com.major.qr.dialog.UploadDialog;
import com.major.qr.viewmodels.DocumentViewModel;

import java.io.File;

public class DocumentActivity extends AppCompatActivity {
    private static final String TAG = DocumentActivity.class.getSimpleName();
    private final int FILE_PICK_REQUEST = 200;
    private final int REQUEST_CAPTURE_IMAGE = 201;
    Uri uri;
    String imageFilePath;
    private ActivityDocumentBinding binding;
    private DocumentViewModel documentViewModel;
    private DocDisplayAdapter displayAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocumentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        documentViewModel = new DocumentViewModel(getApplication());

        documentViewModel.getDocs().observe(this, docs -> {
            displayAdapter = new DocDisplayAdapter(this, docs, documentViewModel);
            binding.docRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.docRecyclerView.setAdapter(displayAdapter);
        });

//        binding.progressCircular.setVisibility(View.GONE);

        binding.uploadDocument.setOnClickListener(view -> {
            UploadDialog uploadDialog = new UploadDialog(documentViewModel);
            uploadDialog.show(getSupportFragmentManager(), "Your Dialog");
//            uploadDialog.show();
        });
//        binding.fileSelect.setOnClickListener(v -> {
//            String[] supportedMimeTypes = {"application/pdf", "application/msword", "image/*"};
//            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.setType("*/*");
//            intent.putExtra(Intent.EXTRA_MIME_TYPES, supportedMimeTypes);
//
//            try {
//                startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_PICK_REQUEST);
//            } catch (ActivityNotFoundException ex) {
//                // Potentially direct the user to the Market with a Dialog
//                Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
//            }
//        });


//        binding.upload.setOnClickListener(v -> {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{
//                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                            Manifest.permission.MANAGE_EXTERNAL_STORAGE
//                    }, 1
//            );
//            // If you have access to the external storage, do whatever you need
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                if (Environment.isExternalStorageManager()) {
//                    // If you don't have access, launch a new activity to show the user the system's dialog
//                    // to allow access to the external storage
//                } else {
//                    Intent intent = new Intent();
//                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                    Uri uri = Uri.fromParts("package", this.getPackageName(), null);
//                    intent.setData(uri);
//                    startActivity(intent);
//                }
//            }
//
//            String path = binding.fileSelect.getText().toString();
//            if (!path.isEmpty() || !path.equals(getResources().getString(R.string.file_name))) {
//                Log.d(TAG, "onCreate: " + uri);
//
//                String[] strings = FileUtils.getPath(this, uri).split("/");
//                StringBuilder builder = new StringBuilder();
//                for (int i = 0; i < strings.length - 1; ++i) {
//                    builder.append("/");
//                    builder.append(strings[i]);
//                }
//                String filePath = builder.toString();
//                String fileName = strings[strings.length - 1];
//                Log.d(TAG, "onCreate: " + filePath);
//
//                File file = new File(filePath, fileName);
//                Log.d(TAG, "onCreate: " + file.getAbsolutePath());
//                documentViewModel.uploadDoc(file);
//            } else
//                Toast.makeText(this, "Select a file first!", Toast.LENGTH_SHORT).show();
//        });

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == FILE_PICK_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                // Get the Uri of the selected file
//                uri = data.getData();
//                Log.d(TAG, "File Uri: " + uri.toString());
//                String path;
//                String[] projection = {MediaStore.Files.FileColumns.DATA};
//                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//                if (cursor == null) {
//                    path = uri.getPath();
//                } else {
//                    cursor.moveToFirst();
//                    int column_index = cursor.getColumnIndexOrThrow(projection[0]);
//                    path = cursor.getString(column_index);
//                    cursor.close();
//                }
//                if (path == null || path.isEmpty()) {
//                    Log.d("uri.getPath() = ", uri.getPath());
//                    binding.fileSelect.setText(uri.getPath());
//                } else {
//                    Log.d("path = ", path);
//                    binding.fileSelect.setText(path);
//                }
//            }
//        }
//        if (requestCode == REQUEST_CAPTURE_IMAGE) {
//            Log.d(requestCode + "", resultCode + "");
//            if (resultCode == RESULT_OK) {
//                Toast.makeText(this, "Captured! Click on Upload to upload the image.", Toast.LENGTH_SHORT).show();
//                binding.fileSelect.setText(imageFilePath);
//                uri = Uri.fromFile(new File(imageFilePath));
//                Log.d(TAG, "onActivityResult: " + uri);
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//
//    }
}
