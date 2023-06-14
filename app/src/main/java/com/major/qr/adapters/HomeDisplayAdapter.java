package com.major.qr.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.major.qr.R;
import com.major.qr.pojo.Doc;
import com.major.qr.viewmodels.QrLinkViewModel;

import java.util.List;

public class HomeDisplayAdapter extends RecyclerView.Adapter<HomeDisplayAdapter.ItemViewHolder> {
    public List<Doc> list;
    public Context context;
    QrLinkViewModel viewModel;

    public HomeDisplayAdapter(Context context, List<Doc> list, QrLinkViewModel viewModel) {
        this.list = list;
        this.context = context;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public HomeDisplayAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_doc, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeDisplayAdapter.ItemViewHolder holder, int position) {
        Doc d = list.get(position);
        holder.s1.setText(d.getDocumentType());
        holder.s2.setText(d.getDocumentReference());
        holder.s3.setText(d.getDocumentId());
        holder.checkBox.setChecked(true);
        holder.checkBox.setClickable(false);
        holder.checkBox.setButtonTintList(ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.zero_blue)));
        holder.checkBox.setVisibility(View.GONE);
        holder.deleteButton.setOnClickListener(view -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        viewModel.deleteQrLink(list.get(position).getDocumentId()) // QrId
                                .observe((LifecycleOwner) context, jsonObject -> {
                                    Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_SHORT).show();
                                    list.remove(position);
                                    notifyItemRemoved(position);

                                });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        });
        // Buttons gone
        holder.b1.setVisibility(View.GONE);
        holder.b2.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView s1, s2, s3;
        CheckBox checkBox;
        Button b1, b2;
        ImageButton deleteButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            s1 = itemView.findViewById(R.id.textview1);
            s2 = itemView.findViewById(R.id.textview2);
            s3 = itemView.findViewById(R.id.textview3);
            checkBox = itemView.findViewById(R.id.checkbox);
            b1 = itemView.findViewById(R.id.update_button);
            b2 = itemView.findViewById(R.id.download_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
