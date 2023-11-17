package com.major.qr.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.major.qr.databinding.DialogCreateqrBinding
import com.major.qr.viewmodels.QrLinkViewModel
import org.json.JSONException
import org.json.JSONObject
import java.util.Arrays

class CreateQRDialog(var docs: HashSet<String?>, var viewModel: QrLinkViewModel) : DialogFragment() {
    var binding: DialogCreateqrBinding? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        binding = DialogCreateqrBinding.inflate(layoutInflater)
        builder.setView(binding!!.root)
        val sessionTypes = arrayOf("OTA", "Other", "Other")
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sessionTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.sessionType.adapter = adapter
        binding!!.create.setOnClickListener { view: View? ->
            if (binding!!.sessionName.text == null || binding!!.validTime.text == null) {
                Toast.makeText(
                    context,
                    "Please enter session name and valid time",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val sessionType = binding!!.sessionType.selectedItem.toString()
            val sessionName = binding!!.sessionName.text.toString()
            val validTime = binding!!.validTime.text.toString()
            viewModel.createQr(docs, sessionName, sessionType, validTime)
                .observe(this) { jsonObject: JSONObject? ->
                    try {
                        showQr(jsonObject!!.getString("token"))
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    Toast.makeText(context, "QR Link created", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
        }
        binding!!.docs.text = Arrays.toString(docs.toTypedArray())
        val dialog: Dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    private fun showQr(token: String) {
        val builder = Dialog(requireContext(), android.R.style.Theme_Light)
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        builder.window!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        builder.setOnDismissListener { dialogInterface: DialogInterface? -> }
        val imageView = ImageView(context)
        builder.addContentView(
            imageView,
            RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
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
}