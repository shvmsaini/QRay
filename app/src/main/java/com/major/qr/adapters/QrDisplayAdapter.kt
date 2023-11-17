package com.major.qr.adapters

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.major.qr.R
import com.major.qr.models.Qr
import com.major.qr.ui.DashboardActivity
import com.major.qr.ui.HomeFragment
import com.major.qr.viewmodels.QrLinkViewModel
import org.json.JSONObject
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

class QrDisplayAdapter(
    var context: Context,
    var list: ArrayList<Qr>,
    var viewModel: QrLinkViewModel
) :
    RecyclerView.Adapter<QrDisplayAdapter.ItemViewHolder>() {
    val TAG = QrDisplayAdapter::class.java.simpleName
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_qr, parent, false)
        return ItemViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val (lastSeen, _, sessionName, _, sessionValidTime, qrId, token) = list[position]
        if (lastSeen != HomeFragment.NOT_ACCESSED_YET) {
            val duration = Duration.between(
                LocalDateTime.parse(lastSeen), LocalDateTime.now(ZoneOffset.UTC)
            )
            val seconds = duration.seconds
            val minutes = duration.toMinutes()
            val hours = duration.toHours()
            val days = duration.toDays()
            if (seconds <= 100) holder.lastSeen.text =
                "$seconds Seconds Ago" else if (minutes < 60L || hours == 1L) holder.lastSeen.text =
                "$minutes Minutes Ago" else if (hours <= 23L) holder.lastSeen.text =
                "$hours Hours Ago" else holder.lastSeen.text = "$days Days Ago"
        } else holder.lastSeen.text = HomeFragment.NOT_ACCESSED_YET
        holder.qrName.text = sessionName
        holder.qrId.text = qrId
        holder.sessionValidTime.text = sessionValidTime
        val duration = Duration.between(
            LocalDateTime.now(ZoneOffset.UTC), LocalDateTime.parse(
                sessionValidTime
            )
        )
        val seconds = duration.seconds
        val minutes = duration.toMinutes()
        val hours = duration.toHours()
        val days = duration.toDays()
        if (seconds <= 0) {
            holder.itemView.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.delete)
            )
            holder.sessionValidTime.setText(R.string.expired)
        } else if (minutes <= 100L) holder.sessionValidTime.text =
            "$minutes Minutes" else if (hours <= 100L) holder.sessionValidTime.text =
            "$hours Hours" else holder.sessionValidTime.text = "$days Days"
        holder.deleteButton.setOnClickListener { view: View? ->
            val dialogClickListener =
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> viewModel.deleteQrLink(
                            list[position].qrId!!
                        )
                            .observe((context as LifecycleOwner)) { jsonObject: JSONObject? ->
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
                .setNegativeButton("No", dialogClickListener).show()
        }
        holder.qrButton.setOnClickListener { view: View? ->
            val builder = Dialog(context, android.R.style.Theme_Light)
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
            builder.window!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))
            builder.setOnDismissListener { dialogInterface: DialogInterface? -> }
            //            Button button = new Button(context);
//            button.setText(R.string.open_in_browser);
//            button.setOnClickListener(v -> {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(INSTANCE + qr.getToken()));
//                context.startActivity(browserIntent);
//            });
            val imageView = ImageView(context)
            builder.addContentView(
                imageView, RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            //            builder.addContentView(button, new RelativeLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            val writer = QRCodeWriter()
            try {
                val bitMatrix = writer.encode(token, BarcodeFormat.QR_CODE, 512, 512)
                val width = bitMatrix.width
                val height = bitMatrix.height
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                for (x in 0 until width) for (y in 0 until height) bmp.setPixel(
                    x,
                    y,
                    if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                )
                imageView.setImageBitmap(bmp)
                builder.show()
            } catch (e: WriterException) {
                e.printStackTrace()
            }
        }
        holder.shareButton.setOnClickListener { view: View? ->
            val intent = Intent(Intent.ACTION_SEND)
            /*This will be the actual content you wish you share.*/
            val shareBody = DashboardActivity.INSTANCE + token
            /*The type of the content is text, obviously.*/intent.type = "text/plain"
            /*Applying information Subject and Body.*/intent.putExtra(
            Intent.EXTRA_SUBJECT,
            "My documents"
        )
            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            /*Fire!*/context.startActivity(Intent.createChooser(intent, "Choose"))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var lastSeen: TextView
        var qrName: TextView
        var qrId: TextView
        var sessionValidTime: TextView
        var qrButton: ImageButton
        var shareButton: ImageButton
        var deleteButton: ImageButton

        init {
            lastSeen = itemView.findViewById(R.id.doc_type)
            qrName = itemView.findViewById(R.id.doc_name)
            qrId = itemView.findViewById(R.id.doc_id)
            deleteButton = itemView.findViewById(R.id.delete_button)
            qrButton = itemView.findViewById(R.id.qr_button)
            sessionValidTime = itemView.findViewById(R.id.session_valid_time_view)
            shareButton = itemView.findViewById(R.id.share_button)
        }
    }
}