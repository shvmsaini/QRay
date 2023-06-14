package com.major.qr.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.major.qr.adapters.HomeDisplayAdapter;
import com.major.qr.databinding.HomeBinding;
import com.major.qr.pojo.Doc;
import com.major.qr.pojo.Qr;
import com.major.qr.viewmodels.QrLinkViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    QrLinkViewModel viewModel;
    HomeBinding binding;
    HomeDisplayAdapter homeDisplayAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = HomeBinding.inflate(getLayoutInflater());
        viewModel = new QrLinkViewModel(requireActivity().getApplication());

        viewModel.getQrLink().observe(requireActivity(), jsonArray -> {
            ArrayList<Doc> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); ++i) {
                try {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Doc d = new Doc();
                    d.setDocumentId(object.get("id").toString());
                    d.setDocumentReference(object.getString("sessionName"));
                    d.setDocumentType(object.getString("sessionValidTime"));
                    list.add(d);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            homeDisplayAdapter = new HomeDisplayAdapter(getContext(), list, viewModel);
            binding.recyclerView.setAdapter(homeDisplayAdapter);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.progressCircular.setVisibility(View.GONE);
        });

        return binding.getRoot();
    }
}