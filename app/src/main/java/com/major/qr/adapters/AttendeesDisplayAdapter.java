package com.major.qr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.major.qr.R;
import com.major.qr.pojo.Attendees;

import java.util.ArrayList;

public class AttendeesDisplayAdapter extends RecyclerView.Adapter<AttendeesDisplayAdapter.ItemViewHolder> {
    ArrayList<Attendees> list;
    Context context;

    public AttendeesDisplayAdapter(Context context, ArrayList<Attendees> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_attendee, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Attendees attendees = list.get(position);
        holder.attendeeName.setText(attendees.getDisplayName());
        holder.attendeeEmail.setText(attendees.getAddedDateTime());
        holder.attendeeTime.setText(attendees.getEmail());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView attendeeName, attendeeEmail, attendeeTime;

        public ItemViewHolder(View itemView) {
            super(itemView);
            attendeeName = itemView.findViewById(R.id.attendee_name);
            attendeeEmail = itemView.findViewById(R.id.attendee_email);
            attendeeTime = itemView.findViewById(R.id.attendee_time);
        }
    }
}
