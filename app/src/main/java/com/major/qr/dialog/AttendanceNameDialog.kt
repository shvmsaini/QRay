package com.major.qr.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import com.major.qr.R
import com.major.qr.adapters.AttendanceDisplayAdapter
import com.major.qr.databinding.DialogAttendanceNameBinding
import com.major.qr.ui.AttendanceFragment
import com.major.qr.viewmodels.AttendanceViewModel
import org.json.JSONObject

class AttendanceNameDialog(
    private var viewModel: AttendanceViewModel,
    private var adapter: AttendanceDisplayAdapter
) : DialogFragment() {
    var binding: DialogAttendanceNameBinding? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        binding = DialogAttendanceNameBinding.inflate(layoutInflater)
        builder.setView(binding!!.root)
        binding!!.create.setOnClickListener { view: View? ->
            val name = binding!!.name.text.toString()
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Enter attendance name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.createAttendance(name)
                .observe((requireContext() as LifecycleOwner)) { jsonObject: JSONObject? ->
                    if (jsonObject != null) {
                        Toast.makeText(
                            requireContext(), "Successfully created attendance register.",
                            Toast.LENGTH_SHORT
                        ).show()
                        adapter.notifyItemInserted(adapter.itemCount)
                        // Restart Fragment
                        parentFragmentManager.beginTransaction().replace(
                            R.id.fragment,
                            AttendanceFragment()
                        ).commit()
                        dismiss()
                    } else {
                        Toast.makeText(
                            requireContext(), "Unable to create attendance register.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
        val dialog: Dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
}