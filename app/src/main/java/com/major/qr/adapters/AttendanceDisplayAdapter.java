package com.major.qr.adapters;

import static com.major.qr.ui.LoginActivity.URL;

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
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.major.qr.R;
import com.major.qr.pojo.Attendance;
import com.major.qr.ui.AttendeesActivity;
import com.major.qr.ui.LoginActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttendanceDisplayAdapter extends RecyclerView.Adapter<AttendanceDisplayAdapter.ItemViewHolder> {
    public static final String TAG = AttendanceDisplayAdapter.class.getSimpleName();
    ArrayList<Attendance> list;
    Context context;

    public AttendanceDisplayAdapter(Context context, ArrayList<Attendance> list) {
        this.context = context;
        this.list = list;
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
            DialogInterface.OnClickListener dialogClickListener =
                    (dialog, which) -> {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                final String url = URL + "/attendance/delete?attendanceId=" + attendance.getId();
                                RequestQueue queue = Volley.newRequestQueue(context);
                                StringRequest request = new StringRequest(Request.Method.DELETE, url, response -> {
                                    Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_SHORT).show();
                                    list.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, getItemCount());
                                }, error -> {
                                    Toast.makeText(context, "Unable to delete!", Toast.LENGTH_SHORT).show();
                                    Log.e("HttpClient", "error: " + error.toString());
                                    holder.itemView.setVisibility(View.GONE);
                                }) {
                                    @Override
                                    public Map<String, String> getHeaders() {
                                        return new HashMap<String, String>() {{
                                            put("Authorization", LoginActivity.ACCESS_TOKEN);
                                        }};
                                    }
                                };
                                queue.add(request);
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
