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

import com.major.qr.R;
import com.major.qr.adapters.DocDisplayAdapter;
import com.major.qr.databinding.DocumentLayoutBinding;
import com.major.qr.pojo.Doc;
import com.major.qr.viewmodels.DocumentViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class DocumentActivity extends AppCompatActivity {
    private static final String TAG = DocumentActivity.class.getSimpleName();
    private final int FILE_PICK_REQUEST = 200;
    private final int REQUEST_CAPTURE_IMAGE = 201;
    Uri uri;
    String imageFilePath;
    private DocumentLayoutBinding binding;
    private DocumentViewModel documentViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DocumentLayoutBinding.inflate(getLayoutInflater());
        documentViewModel = new DocumentViewModel(getApplication());
        setContentView(binding.getRoot());
        ArrayList<Doc> list = new ArrayList<>();
        Doc d = new Doc("HOLA", "AMIGO", "!", true);
        for (int i = 0; i < 5; ++i)
            list.add(d);
        DocDisplayAdapter displayAdapter = new DocDisplayAdapter(this, list);
        binding.docRecyclerView.setAdapter(displayAdapter);
        binding.docRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        binding.progressCircular.setVisibility(View.GONE);

        binding.fileSelect.setOnClickListener(v -> {
            String[] supportedMimeTypes = {"application/pdf", "application/msword", "image/*"};
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, supportedMimeTypes);

            try {
                startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_PICK_REQUEST);
            } catch (android.content.ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.upload.setOnClickListener(v -> {
            String file = binding.fileSelect.getText().toString();
            if (!file.isEmpty() || !file.equals(getResources().getString(R.string.file_name))) {
                JSONObject mp = new JSONObject();
                try {
                    mp.put("userId", LoginActivity.USERID);
                    mp.put("documentReference", file);
                    mp.put("documentType", "image");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                documentViewModel.uploadDoc(new File(uri.getPath()));
            }
            else
                Toast.makeText(this, "Select a file first!", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_PICK_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Get the Uri of the selected file
                uri = data.getData();
                Log.d("f", "File Uri: " + uri.toString());
                String path;
                String[] projection = {MediaStore.Files.FileColumns.DATA};
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
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
                Toast.makeText(this, "Captured! Click on Upload to upload the image.", Toast.LENGTH_SHORT).show();
                binding.fileSelect.setText(imageFilePath);
                uri = Uri.fromFile(new File(imageFilePath));
                Log.d(TAG, "onActivityResult: " + uri);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}
