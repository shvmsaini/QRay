package com.major.qr.ui

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.major.qr.adapters.AttendeesDisplayAdapter
import com.major.qr.databinding.ActivityAttendeesBinding
import com.major.qr.models.Attendee
import com.major.qr.viewmodels.AttendeesViewModel
import org.json.JSONException
import org.json.JSONObject

class AttendeesActivity : AppCompatActivity() {
    val TAG = AttendeesActivity::class.java.simpleName
    var viewModel: AttendeesViewModel? = null
    private var binding: ActivityAttendeesBinding? = null
    private var adapter: AttendeesDisplayAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendeesBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        val bundle = intent.extras
        val AttendanceId = bundle!!.getString("Id")
        val data: JSONObject = object : JSONObject() {
            init {
                try {
                    put("ID", AttendanceId)
                    put("UID", LoginActivity.USERID)
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
            }
        }
        viewModel = ViewModelProvider(this).get(AttendeesViewModel::class.java)
        viewModel!!.getAttendees(AttendanceId!!)
            .observe(this) { attendees: ArrayList<Attendee> ->
                adapter = AttendeesDisplayAdapter(this, attendees, viewModel!!, AttendanceId)
                binding!!.recyclerView.layoutManager = LinearLayoutManager(this)
                binding!!.recyclerView.addItemDecoration(
                    MaterialDividerItemDecoration(
                        this,
                        LinearLayoutManager.VERTICAL
                    )
                )
                binding!!.progressCircular.visibility = View.GONE
                if (attendees.size == 0) {
                    binding!!.emptyView.visibility = View.VISIBLE
                    binding!!.refresh.visibility = View.VISIBLE
                } else {
                    binding!!.emptyView.visibility = View.GONE
                    binding!!.refresh.visibility = View.GONE
                }
                binding!!.recyclerView.adapter = adapter
            }
        binding!!.showQr.setOnClickListener { v: View? ->
            val builder = Dialog(this, android.R.style.Theme_Light)
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
            builder.window!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))
            builder.setOnDismissListener { dialogInterface: DialogInterface? -> }
            val imageView = ImageView(this)
            builder.addContentView(
                imageView,
                RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            val writer = QRCodeWriter()
            try {
                val bitMatrix = writer.encode(data.toString(), BarcodeFormat.QR_CODE, 512, 512)
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
        binding!!.refresh.setOnClickListener { view: View? ->
            binding!!.progressCircular.visibility = View.VISIBLE
            binding!!.emptyView.visibility = View.GONE
            binding!!.refresh.visibility = View.GONE
            viewModel!!.getAttendees(AttendanceId)
        }
        binding!!.topAppBar.setNavigationOnClickListener { view: View? -> onBackPressed() }
    }
}