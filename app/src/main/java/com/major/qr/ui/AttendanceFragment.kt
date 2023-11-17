package com.major.qr.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.major.qr.adapters.AttendanceDisplayAdapter
import com.major.qr.databinding.FragmentAttendanceBinding
import com.major.qr.dialog.AttendanceNameDialog
import com.major.qr.models.Attendance
import com.major.qr.viewmodels.AttendanceViewModel

class AttendanceFragment : Fragment() {
    private val TAG = AttendanceFragment::class.java.simpleName
    var viewModel: AttendanceViewModel? = null
    var binding: FragmentAttendanceBinding? = null
    private var adapter: AttendanceDisplayAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAttendanceBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity())[AttendanceViewModel::class.java]
        viewModel?.attendances?.observe(viewLifecycleOwner) { attendances: ArrayList<Attendance> ->
            adapter = AttendanceDisplayAdapter(requireContext(), attendances, viewModel!!)
            binding?.recyclerView?.layoutManager = LinearLayoutManager(requireContext())
            binding?.progressCircular?.visibility = View.GONE
            if (attendances.size == 0) binding!!.emptyView.visibility =
                View.VISIBLE else binding!!.emptyView.visibility = View.GONE
            binding!!.recyclerView.adapter = adapter
        }
        binding!!.createAttendance.setOnClickListener {
            val nameDialog = AttendanceNameDialog(
                viewModel!!, adapter!!
            )
            nameDialog.show(requireActivity().supportFragmentManager, "Attendance Name Dialog")
        }
        return binding!!.root
    }
}