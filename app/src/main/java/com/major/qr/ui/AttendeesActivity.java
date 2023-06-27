package com.major.qr.ui;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.divider.MaterialDividerItemDecoration;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.major.qr.adapters.AttendeesDisplayAdapter;
import com.major.qr.databinding.ActivityAttendeesBinding;
import com.major.qr.viewmodels.AttendeesViewModel;

import org.json.JSONException;
import org.json.JSONObject;

public class AttendeesActivity extends AppCompatActivity {
    public final String TAG = AttendeesActivity.class.getSimpleName();
    AttendeesViewModel viewModel;
    private ActivityAttendeesBinding binding;
    private AttendeesDisplayAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendeesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        final String AttendanceId = bundle.getString("Id");
        JSONObject data = new JSONObject() {{
            try {
                put("ID", AttendanceId);
                put("UID", LoginActivity.USERID);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }};

        viewModel = new ViewModelProvider(this).get(AttendeesViewModel.class);
        viewModel.getAttendees(AttendanceId).observe(this, attendees -> {
            adapter = new AttendeesDisplayAdapter(this, attendees, viewModel, AttendanceId);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerView.addItemDecoration(new MaterialDividerItemDecoration(this,
                    LinearLayoutManager.VERTICAL));
            binding.progressCircular.setVisibility(View.GONE);
            if (attendees.size() == 0) {
                binding.emptyView.setVisibility(View.VISIBLE);
                binding.refresh.setVisibility(View.VISIBLE);
            } else {
                binding.emptyView.setVisibility(View.GONE);
                binding.refresh.setVisibility(View.GONE);
            }
            binding.recyclerView.setAdapter(adapter);
        });

        binding.showQr.setOnClickListener(v -> {
            Dialog builder = new Dialog(this, android.R.style.Theme_Light);
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            builder.setOnDismissListener(dialogInterface -> {
                // NOTHING
            });

            ImageView imageView = new ImageView(this);
            builder.addContentView(imageView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            QRCodeWriter writer = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = writer.encode(String.valueOf(data), BarcodeFormat.QR_CODE, 512, 512);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++)
                    for (int y = 0; y < height; y++)
                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);

                imageView.setImageBitmap(bmp);
                builder.show();

            } catch (WriterException e) {
                e.printStackTrace();
            }
        });

        binding.refresh.setOnClickListener(view -> {
            binding.progressCircular.setVisibility(View.VISIBLE);
            binding.emptyView.setVisibility(View.GONE);
            binding.refresh.setVisibility(View.GONE);
            viewModel.getAttendees(AttendanceId);
        });

        binding.topAppBar.setNavigationOnClickListener(view -> onBackPressed());
    }

}
