package com.major.qr.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.major.qr.R;
import com.major.qr.dialog.UploadDialog;
import com.major.qr.pojo.Doc;
import com.major.qr.viewmodels.DocumentViewModel;

import java.util.List;

public class DocDisplayAdapter extends RecyclerView.Adapter<DocDisplayAdapter.ItemViewHolder> {
    public static final String TAG = DocDisplayAdapter.class.getSimpleName();
    public List<Doc> list;
    public Context context;
    public DocumentViewModel documentViewModel;

    public DocDisplayAdapter(Context context, List<Doc> list, DocumentViewModel documentViewModel) {
        Log.d(TAG, "Running Constructor");
        this.context = context;
        this.list = list;
        this.documentViewModel = documentViewModel;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_doc, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Doc d = list.get(position);
        holder.s1.setText(d.getDocumentType());
        holder.s2.setText(d.getDocumentReference());
        holder.s3.setText(d.getDocumentId());
        holder.c1.setVisibility(View.GONE);
//        holder.c1.setChecked(d.getState());

        // Buttons gone
        holder.updateButton.setVisibility(View.VISIBLE);
        holder.downloadButton.setVisibility(View.VISIBLE);
        holder.downloadButton.setOnClickListener(view -> {
            documentViewModel.getDocLink(d.getDocumentReference()).observe((LifecycleOwner) context, s -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s.substring(1, s.length() - 1)));
                context.startActivity(browserIntent);
            });
        });
        holder.updateButton.setOnClickListener(view -> {
            UploadDialog uploadDialog = new UploadDialog(d.getDocumentReference(), documentViewModel);
            uploadDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "Your Dialog");
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView s1, s2, s3;
        CheckBox c1;
        Button updateButton, downloadButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            s1 = itemView.findViewById(R.id.textview1);
            s2 = itemView.findViewById(R.id.textview2);
            s3 = itemView.findViewById(R.id.textview3);
            c1 = itemView.findViewById(R.id.checkbox);
            updateButton = itemView.findViewById(R.id.update_button);
            downloadButton = itemView.findViewById(R.id.download_button);
        }
    }
}
