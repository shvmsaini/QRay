package com.major.qr.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.major.qr.R;
import com.major.qr.models.Attendee;
import com.major.qr.viewmodels.AttendeesViewModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AttendeesDisplayAdapter extends RecyclerView.Adapter<AttendeesDisplayAdapter.ItemViewHolder> {
    public final String TAG = AttendeesDisplayAdapter.class.getSimpleName();
    String attendanceId;
    ArrayList<Attendee> list;
    Context context;
    AttendeesViewModel viewModel;

    public AttendeesDisplayAdapter(Context context, ArrayList<Attendee> list,
                                   AttendeesViewModel viewModel, String attendanceId) {
        this.list = list;
        this.context = context;
        this.viewModel = viewModel;
        this.attendanceId = attendanceId;
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
        Attendee attendee = list.get(position);
        holder.attendeeName.setText(attendee.getDisplayName());
        holder.attendeeEmail.setText(attendee.getEmail());
        final String addedDateTime = LocalDateTime.parse(attendee.getAddedDateTime()
                        , DateTimeFormatter
                                .ofPattern("yyyy-MM-dd HH:mm:ss"))
                .format(DateTimeFormatter
                        .ofPattern("yyyy MMM d, KK:mm:ss"));
        holder.attendeeTime.setText(addedDateTime);
        holder.deleteAttendee.setOnClickListener(view -> {
            DialogInterface.OnClickListener dialogClickListener = (dialogInterface, i) -> {
                switch (i) {
                    case DialogInterface.BUTTON_POSITIVE:
                        viewModel.removeAttendee(attendee.getRecordId(), attendanceId)
                                .observe((LifecycleOwner) context, jsonObject -> {
                                    Toast.makeText(context, "Successfully removed!", Toast.LENGTH_SHORT).show();
                                    list.remove(position);
                                    this.notifyItemRemoved(position);
                                });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                }
            };
            new AlertDialog.Builder(context).setMessage("Are you sure?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView attendeeName, attendeeEmail, attendeeTime;
        ImageButton deleteAttendee;

        public ItemViewHolder(View itemView) {
            super(itemView);
            attendeeName = itemView.findViewById(R.id.attendee_name);
            attendeeEmail = itemView.findViewById(R.id.attendee_email);
            attendeeTime = itemView.findViewById(R.id.attendee_time);
            deleteAttendee = itemView.findViewById(R.id.delete_attendee);
        }
    }
}
