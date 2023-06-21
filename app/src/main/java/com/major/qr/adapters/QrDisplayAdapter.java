package com.major.qr.adapters;

import static com.major.qr.ui.DashboardActivity.INSTANCE;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.major.qr.R;
import com.major.qr.models.Qr;
import com.major.qr.viewmodels.QrLinkViewModel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class QrDisplayAdapter extends RecyclerView.Adapter<QrDisplayAdapter.ItemViewHolder> {
    public final String TAG = QrDisplayAdapter.class.getSimpleName();
    public List<Qr> list;
    public Context context;
    QrLinkViewModel viewModel;

    public QrDisplayAdapter(Context context, List<Qr> list, QrLinkViewModel viewModel) {
        this.list = list;
        this.context = context;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public QrDisplayAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_qr, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QrDisplayAdapter.ItemViewHolder holder, int position) {
        Qr qr = list.get(position);
        holder.lastSeen.setText(qr.getLastSeen());
        holder.qrName.setText(qr.getSessionName());
        holder.qrId.setText(qr.getQrId());
        holder.sessionValidTime.setText(qr.getSessionValidTime());
        Duration duration = Duration.between(LocalDateTime.now(), LocalDateTime.parse(qr.getSessionValidTime()));
        long seconds = duration.getSeconds();
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();
        if (seconds <= 0) {
            holder.itemView.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.delete)));
            holder.sessionValidTime.setText(R.string.expired);
        }
        else if (minutes <= 100L) holder.sessionValidTime.setText(minutes + " Minutes Remains");
        else if (hours <= 100L) holder.sessionValidTime.setText(hours + " Hours Remains");
        else holder.sessionValidTime.setText(days + " Days Remains");
        holder.deleteButton.setOnClickListener(view -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        viewModel.deleteQrLink(list.get(position).getQrId())
                                .observe((LifecycleOwner) context, jsonObject -> {
                                    Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_SHORT).show();
                                    list.remove(position);
                                    notifyItemRemoved(position);
                                });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                }
            };
            new AlertDialog.Builder(context).setMessage("Are you sure?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        });
        holder.qrButton.setOnClickListener(view -> {
            Dialog builder = new Dialog(context, android.R.style.Theme_Light);
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            builder.setOnDismissListener(dialogInterface -> {
            });
            Button button = new Button(context);
            button.setText(R.string.open_in_browser);
            button.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(INSTANCE + qr.getToken()));
                context.startActivity(browserIntent);
            });

            ImageView imageView = new ImageView(context);
            builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            builder.addContentView(button, new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            QRCodeWriter writer = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = writer.encode(qr.getToken(), BarcodeFormat.QR_CODE, 512, 512);
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView lastSeen, qrName, qrId, sessionValidTime;
        ImageButton qrButton;
        ImageButton deleteButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            lastSeen = itemView.findViewById(R.id.doc_type);
            qrName = itemView.findViewById(R.id.doc_name);
            qrId = itemView.findViewById(R.id.doc_id);
            deleteButton = itemView.findViewById(R.id.delete_button);
            qrButton = itemView.findViewById(R.id.qr_button);
            sessionValidTime = itemView.findViewById(R.id.session_valid_time_view);
        }
    }
}
