package com.major.qr.adapters

import android.content.Context
import android.content.DialogInterface
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
import com.major.qr.models.Attendee
import com.major.qr.viewmodels.AttendeesViewModel
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AttendeesDisplayAdapter(
    var context: Context, var list: ArrayList<Attendee>,
    var viewModel: AttendeesViewModel, var attendanceId: String
) : RecyclerView.Adapter<AttendeesDisplayAdapter.ItemViewHolder>() {
    val TAG = AttendeesDisplayAdapter::class.java.simpleName
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_attendee, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val (addedDateTime1, displayName, _, email, recordId) = list[position]
        holder.attendeeName.text = displayName
        holder.attendeeEmail.text = email
        val addedDateTime = LocalDateTime.parse(
            addedDateTime1, DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
        )
            .format(
                DateTimeFormatter
                    .ofPattern("yyyy MMM d, KK:mm:ss")
            )
        holder.attendeeTime.text = addedDateTime
        holder.deleteAttendee.setOnClickListener { view: View? ->
            val dialogClickListener =
                DialogInterface.OnClickListener { dialogInterface: DialogInterface?, i: Int ->
                    when (i) {
                        DialogInterface.BUTTON_POSITIVE -> viewModel.removeAttendee(
                            recordId, attendanceId
                        )
                            .observe((context as LifecycleOwner)) { jsonObject: JSONObject? ->
                                Toast.makeText(context, "Successfully removed!", Toast.LENGTH_SHORT)
                                    .show()
                                list.removeAt(position)
                                notifyItemRemoved(position)
                            }

                        DialogInterface.BUTTON_NEGATIVE -> {}
                    }
                }
            AlertDialog.Builder(context).setMessage("Are you sure?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var attendeeName: TextView
        var attendeeEmail: TextView
        var attendeeTime: TextView
        var deleteAttendee: ImageButton

        init {
            attendeeName = itemView.findViewById(R.id.attendee_name)
            attendeeEmail = itemView.findViewById(R.id.attendee_email)
            attendeeTime = itemView.findViewById(R.id.attendee_time)
            deleteAttendee = itemView.findViewById(R.id.delete_attendee)
        }
    }
}