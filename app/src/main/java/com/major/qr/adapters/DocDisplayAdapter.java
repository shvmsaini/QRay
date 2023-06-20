package com.major.qr.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.major.qr.R;
import com.major.qr.databinding.FragmentDocumentBinding;
import com.major.qr.dialog.UploadDialog;
import com.major.qr.models.Doc;
import com.major.qr.viewmodels.DocumentViewModel;

import java.util.HashSet;
import java.util.List;

public class DocDisplayAdapter extends RecyclerView.Adapter<DocDisplayAdapter.ItemViewHolder> {
    public final String TAG = DocDisplayAdapter.class.getSimpleName();
    public List<Doc> list;
    public Context context;
    public DocumentViewModel documentViewModel;

    public HashSet<String> selectedDocs;
    FragmentDocumentBinding binding;

    public DocDisplayAdapter(Context context, List<Doc> list, DocumentViewModel documentViewModel,
                             FragmentDocumentBinding binding) {
        this.context = context;
        this.list = list;
        this.documentViewModel = documentViewModel;
        this.binding = binding;
        selectedDocs = new HashSet<>();
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
        Doc doc = list.get(position);
        holder.docType.setText(doc.getDocumentType());
        holder.docId.setText(doc.getDocumentReference());
        holder.docRef.setText(doc.getDocumentId());
        holder.updateButton.setVisibility(View.VISIBLE);
        holder.downloadButton.setVisibility(View.VISIBLE);
        holder.downloadButton.setOnClickListener(view -> documentViewModel.getDocLink(doc.getDocumentReference())
                .observe((LifecycleOwner) context, s -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s.substring(1, s.length() - 1)));
                    context.startActivity(browserIntent);
                }));

        holder.itemView.setOnLongClickListener(view -> {
            if (selectedDocs.contains(list.get(position).getDocumentId())) {
                holder.itemView.performClick();
                return false;
            }
            view.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.teal_700)));
            selectedDocs.add(list.get(position).getDocumentId());
            binding.clearAll.setVisibility(View.VISIBLE);
            return true;
        });

        holder.itemView.setOnClickListener(view -> {
            if (selectedDocs.size() == 0) {
                return;
            }
            if (selectedDocs.contains(list.get(position).getDocumentId())) {
                view.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.secondary_blue)));
                selectedDocs.remove(list.get(position).getDocumentId());
                if (selectedDocs.size() == 0) {
                    binding.clearAll.setVisibility(View.GONE);
                }
            } else {
                view.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.teal_700)));
                selectedDocs.add(list.get(position).getDocumentId());
            }
        });

        holder.updateButton.setOnClickListener(view -> {
            UploadDialog uploadDialog = new UploadDialog(doc.getDocumentReference(), documentViewModel);
            uploadDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "Your Dialog");
        });

        holder.deleteButton.setOnClickListener(view -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        documentViewModel.deleteDoc(doc.getDocumentId(), doc.getDocumentReference())
                                .observe((LifecycleOwner) context, s -> {
                                    Toast.makeText(context, "Deleted successfully!",
                                            Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView docType, docId, docRef;
        Button updateButton, downloadButton;
        ImageButton deleteButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            docType = itemView.findViewById(R.id.last_seen);
            docId = itemView.findViewById(R.id.qr_name);
            docRef = itemView.findViewById(R.id.qr_id);
            updateButton = itemView.findViewById(R.id.update_button);
            downloadButton = itemView.findViewById(R.id.download_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
