package com.major.qr.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.major.qr.adapters.DocDisplayAdapter
import com.major.qr.databinding.FragmentDocumentBinding
import com.major.qr.dialog.CreateQRDialog
import com.major.qr.dialog.UploadDialog
import com.major.qr.models.Doc
import com.major.qr.viewmodels.DocumentViewModel
import com.major.qr.viewmodels.QrLinkViewModel

class DocumentFragment : Fragment() {
    private val TAG = DocumentFragment::class.java.simpleName
    private var binding: FragmentDocumentBinding? = null
    private var documentViewModel: DocumentViewModel? = null
    private var displayAdapter: DocDisplayAdapter? = null
    private var qrLinkViewModel: QrLinkViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        binding = FragmentDocumentBinding.inflate(layoutInflater)
        qrLinkViewModel = ViewModelProvider(requireActivity()).get(QrLinkViewModel::class.java)
        documentViewModel = ViewModelProvider(requireActivity()).get(
            DocumentViewModel::class.java
        )
        documentViewModel!!.docs.observe(
            viewLifecycleOwner,
            Observer<ArrayList<Doc>> { docs: ArrayList<Doc> ->
                displayAdapter = DocDisplayAdapter(
                    requireContext(), docs, documentViewModel!!, binding!!
                )
                binding!!.docRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding!!.docRecyclerView.adapter = displayAdapter
                binding!!.docRecyclerView.addItemDecoration(
                    MaterialDividerItemDecoration(
                        requireContext(),
                        LinearLayoutManager.VERTICAL
                    )
                )
                binding!!.progressCircular.visibility = View.GONE
                if (docs.size == 0) binding!!.emptyView.visibility =
                    View.VISIBLE else binding!!.emptyView.visibility = View.GONE
            })
        binding!!.uploadDocument.setOnClickListener { view: View? ->
            if (displayAdapter!!.selectedDocs.size > 0) {
                val qrDialog = CreateQRDialog(displayAdapter!!.selectedDocs, qrLinkViewModel!!)
                qrDialog.show(requireActivity().supportFragmentManager, "Your Dialog")
            } else {
                val uploadDialog = UploadDialog(documentViewModel!!)
                uploadDialog.show(requireActivity().supportFragmentManager, "Your Dialog")
            }
        }
        binding!!.clearAll.setOnClickListener { view: View? ->
            binding!!.docRecyclerView.adapter = displayAdapter
            displayAdapter!!.selectedDocs.clear()
            binding!!.clearAll.visibility = View.GONE
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d(TAG, "handleOnBackPressed() called")
                    if (displayAdapter!!.selectedDocs.size > 0) {
                        binding!!.clearAll.performClick()
                    } else {
                        // OnBackPressed causing infinite loop
                        requireActivity().finish()
                    }
                }
            })
        return binding!!.root
    }
}