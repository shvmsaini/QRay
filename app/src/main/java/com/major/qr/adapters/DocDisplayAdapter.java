package com.major.qr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.major.qr.R;
import com.major.qr.pojo.Doc;

import java.util.List;

public class DocDisplayAdapter extends RecyclerView.Adapter<DocDisplayAdapter.ItemViewHolder> {
    public List<Doc> list;
    public Context context;

    public DocDisplayAdapter(Context context, List<Doc> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.doc_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Doc d = list.get(position);
        holder.s1.setText(d.getS1());
        holder.s2.setText(d.getS2());
        holder.s3.setText(d.getS3());
        holder.c1.setVisibility(View.GONE);
        holder.c1.setChecked(d.getState());

        // Buttons gone
        holder.b1.setVisibility(View.VISIBLE);
        holder.b2.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView s1, s2, s3;
        CheckBox c1;
        Button b1, b2;

        public ItemViewHolder(View itemView) {
            super(itemView);
            s1 = itemView.findViewById(R.id.textview1);
            s2 = itemView.findViewById(R.id.textview2);
            s3 = itemView.findViewById(R.id.textview3);
            c1 = itemView.findViewById(R.id.checkbox);
            b1 = itemView.findViewById(R.id.update_button);
            b2 = itemView.findViewById(R.id.download_button);
        }
    }
}
