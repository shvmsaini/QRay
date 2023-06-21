package com.major.qr.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.major.qr.R;
import com.major.qr.databinding.FragmentDocumentBinding;
import com.major.qr.dialog.DocumentDetailDialog;
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
        documentViewModel.getDocLink(doc.getDocumentReference()).observe((LifecycleOwner) context, link -> {
            doc.setDocLink(link.substring(1, link.length() - 1));
            Glide.with(context)
                    .load(doc.getDocLink())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.drawable.placeholder_doc)
                    .into(holder.imageView);
        });

        holder.itemView.setOnLongClickListener(view -> {
            if (selectedDocs.contains(list.get(position).getDocumentId())) {
                unSelect(holder, position);
                return false;
            }
            select(holder, position);
            return true;
        });

        holder.itemView.setOnClickListener(view -> {
            if (selectedDocs.size() == 0) {
                DocumentDetailDialog dialog = new DocumentDetailDialog(documentViewModel, doc, this, position);
                dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "Doc Dialog");
                return;
            }
            if (selectedDocs.contains(list.get(position).getDocumentId()))
                unSelect(holder, position);
            else
                select(holder, position);
        });

//        holder.updateButton.setOnClickListener(view -> {
//            UploadDialog uploadDialog = new UploadDialog(doc.getDocumentReference(), documentViewModel);
//            uploadDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "Your Dialog");
//        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void select(ItemViewHolder holder, int position) {
//        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.teal_200));
        holder.itemView.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.teal_200)));
        selectedDocs.add(list.get(position).getDocumentId());
        binding.clearAll.setVisibility(View.VISIBLE);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            holder.itemView.setBackgroundResource(R.drawable.attendance_background);
        }, 400);
    }

    public void unSelect(ItemViewHolder holder, int position) {
        holder.itemView.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.primary_blue)));
        selectedDocs.remove(list.get(position).getDocumentId());
        if (selectedDocs.size() == 0)
            binding.clearAll.setVisibility(View.GONE);
//        holder.itemView.setBackgroundResource(R.drawable.document_background);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            holder.itemView.setBackgroundResource(R.drawable.document_background);
        }, 400);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
