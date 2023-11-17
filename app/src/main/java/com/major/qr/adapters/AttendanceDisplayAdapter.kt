package com.major.qr.adapters

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.major.qr.R
import com.major.qr.models.Attendance
import com.major.qr.ui.AttendeesActivity
import com.major.qr.viewmodels.AttendanceViewModel
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AttendanceDisplayAdapter(
    private val context: Context,
    var list: ArrayList<Attendance>,
    var viewModel: AttendanceViewModel
) : RecyclerView.Adapter<AttendanceDisplayAdapter.ItemViewHolder>() {
    val TAG = AttendanceDisplayAdapter::class.java.simpleName
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_attendance, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val (id, name, totalAttenders, creationDate1) = list[position]
        holder.totalAttendees.text = totalAttenders
        holder.attendanceName.text = name
        val creationDate = LocalDateTime.parse(
            creationDate1,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        )
            .format(DateTimeFormatter.ofPattern("yyyy MMM d, KK:mm:ss"))
        holder.creationDate.text = creationDate
        holder.itemView.setOnClickListener { v: View? ->
            Log.d(TAG, "AttendanceId: $id")
            context.startActivity(
                Intent(context, AttendeesActivity::class.java)
                    .putExtra("Id", id)
            )
        }
        holder.deleteButton.setOnClickListener { v: View? ->
            val dialogClickListener =
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> viewModel.deleteAttendance(
                            id!!
                        ).observe(
                            (context as LifecycleOwner)
                        ) { jsonObject: JSONObject? ->
                            if (jsonObject == null) {
                                Toast.makeText(context, "Unable to delete!", Toast.LENGTH_SHORT)
                                    .show()
                                holder.itemView.visibility = View.GONE
                                return@observe
                            }
                            Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_SHORT)
                                .show()
                            list.removeAt(position)
                            notifyItemRemoved(position)
                        }

                        DialogInterface.BUTTON_NEGATIVE -> {}
                    }
                }
            AlertDialog.Builder(context).setMessage("Are you sure?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var creationDate: TextView
        var attendanceName: TextView
        var totalAttendees: TextView
        var deleteButton: ImageButton

        init {
            creationDate = itemView.findViewById(R.id.creation_date)
            attendanceName = itemView.findViewById(R.id.attendance_name)
            totalAttendees = itemView.findViewById(R.id.total_attendees)
            deleteButton = itemView.findViewById(R.id.delete_attendance)
        }
    }
}