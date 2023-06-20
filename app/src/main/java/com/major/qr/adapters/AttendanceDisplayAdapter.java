package com.major.qr.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
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
import com.major.qr.models.Attendance;
import com.major.qr.ui.AttendeesActivity;
import com.major.qr.viewmodels.AttendanceViewModel;

import java.util.ArrayList;

public class AttendanceDisplayAdapter extends RecyclerView.Adapter<AttendanceDisplayAdapter.ItemViewHolder> {
    public final String TAG = AttendanceDisplayAdapter.class.getSimpleName();
    ArrayList<Attendance> list;
    Context context;
    AttendanceViewModel viewModel;

    public AttendanceDisplayAdapter(Context context, ArrayList<Attendance> list, AttendanceViewModel viewModel) {
        this.context = context;
        this.list = list;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_attendance, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Attendance attendance = list.get(position);
        holder.totalAttendees.setText(attendance.getTotalAttenders());
        holder.attendanceName.setText(attendance.getName());
        holder.creationDate.setText(attendance.getCreationDate());
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, AttendeesActivity.class);
            Log.d(TAG, "onBindViewHolder: " + attendance.getId());
            i.putExtra("Id", attendance.getId());
            context.startActivity(i);
        });

        holder.deleteButton.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        viewModel.deleteAttendance(attendance.getId()).observe((LifecycleOwner) context, jsonObject -> {
                            if (jsonObject == null) {
                                Toast.makeText(context, "Unable to delete!", Toast.LENGTH_SHORT).show();
                                holder.itemView.setVisibility(View.GONE);
                                return;
                            }
                            Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_SHORT).show();
                            list.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, getItemCount());
                        });
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView creationDate, attendanceName, totalAttendees;
        ImageButton deleteButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            creationDate = itemView.findViewById(R.id.creation_date);
            attendanceName = itemView.findViewById(R.id.attendance_name);
            totalAttendees = itemView.findViewById(R.id.total_attendees);
            deleteButton = itemView.findViewById(R.id.delete_attendance);
        }
    }
}
