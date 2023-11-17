package com.major.qr.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.major.qr.adapters.QrDisplayAdapter
import com.major.qr.databinding.FragmentHomeBinding
import com.major.qr.models.Qr
import com.major.qr.viewmodels.QrLinkViewModel
import org.json.JSONArray
import org.json.JSONException

class HomeFragment : Fragment() {
    var viewModel: QrLinkViewModel? = null
    var binding: FragmentHomeBinding? = null
    var qrDisplayAdapter: QrDisplayAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity()).get(QrLinkViewModel::class.java)
        viewModel!!.qrLink.observe(requireActivity(), Observer { jsonArray: JSONArray? ->
            val list = ArrayList<Qr>()
            if (jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
                    try {
                        val `object` = jsonArray.getJSONObject(i)
                        val qr = Qr()
                        qr.qrId = `object`["id"].toString()
                        qr.sessionName = `object`.getString("sessionName")
                        qr.token = `object`.getString("token")
                        if (`object`.has("lastSeen")) qr.lastSeen =
                            `object`.getString("lastSeen") else qr.lastSeen = NOT_ACCESSED_YET
                        qr.sessionValidTime = `object`.getString("sessionValidTime")
                        list.add(qr)
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                }
            }
            qrDisplayAdapter = QrDisplayAdapter(requireContext(), list, viewModel!!)
            binding!!.recyclerView.adapter = qrDisplayAdapter
            binding!!.recyclerView.layoutManager = LinearLayoutManager(context)
            binding!!.progressCircular.visibility = View.GONE
            if (list.size == 0) binding!!.emptyView.visibility = View.VISIBLE
        })
        return binding!!.root
    }

    companion object {
        const val NOT_ACCESSED_YET = "Not Accessed Yet"
        private val TAG = HomeFragment::class.java.simpleName
    }
}