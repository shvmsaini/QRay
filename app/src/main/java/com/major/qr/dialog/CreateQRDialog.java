package com.major.qr.dialog;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.major.qr.databinding.DialogCreateqrBinding;
import com.major.qr.viewmodels.QrLinkViewModel;

import org.json.JSONException;

import java.util.Arrays;
import java.util.HashSet;

public class CreateQRDialog extends DialogFragment {
    DialogCreateqrBinding binding;
    HashSet<String> docs;
    QrLinkViewModel viewModel;

    public CreateQRDialog(HashSet<String> docs, QrLinkViewModel viewModel) {
        this.docs = docs;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        binding = DialogCreateqrBinding.inflate(getLayoutInflater());
        builder.setView(binding.getRoot()).setTitle("Create QR with selected documents");

        String[] sessionTypes = new String[]{"OTA", "Other", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, sessionTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.sessionType.setAdapter(adapter);

        binding.create.setOnClickListener(view -> {
            if (binding.sessionName.getText() == null || binding.validTime.getText() == null) {
                Toast.makeText(getContext(), "Please enter session name and valid time",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.createQr(docs, binding.sessionName.getText().toString(),
                    binding.sessionType.getSelectedItem().toString(),
                    binding.validTime.getText().toString()).observe(this, jsonObject -> {
                try {
                    showQr(jsonObject.getString("token"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Toast.makeText(getContext(), "QR Link created", Toast.LENGTH_SHORT).show();
            });
        });

        binding.docs.setText(Arrays.toString(docs.toArray()));

        return builder.create();
    }

    private void showQr(String token) {
        Dialog builder = new Dialog(getContext(), android.R.style.Theme_Light);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        builder.setOnDismissListener(dialogInterface -> {
        });

        ImageView imageView = new ImageView(getContext());
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(token, BarcodeFormat.QR_CODE, 512, 512);
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
    }
}
