package com.major.qr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.major.qr.R;
import com.major.qr.pojo.Doc;

import java.util.List;

public class HomeDisplayAdapter extends RecyclerView.Adapter<HomeDisplayAdapter.ItemViewHolder> {
    public List<Doc> list;
    public Context context;

    public HomeDisplayAdapter(Context context, List<Doc> docList) {
        list = docList;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeDisplayAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.doc_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeDisplayAdapter.ItemViewHolder holder, int position) {
        Doc d = list.get(position);
        holder.s1.setText(d.getS1());
        holder.s2.setText(d.getS2());
        holder.s3.setText(d.getS3());
        holder.c1.setChecked(d.getState());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView s1, s2, s3;
        CheckBox c1;

        public ItemViewHolder(View itemView) {
            super(itemView);
            s1 = itemView.findViewById(R.id.textview1);
            s2 = itemView.findViewById(R.id.textview2);
            s3 = itemView.findViewById(R.id.textview3);
            c1 = itemView.findViewById(R.id.checkbox);
        }
    }
}
