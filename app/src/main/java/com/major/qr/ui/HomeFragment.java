package com.major.qr.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.major.qr.adapters.QrDisplayAdapter;
import com.major.qr.databinding.FragmentHomeBinding;
import com.major.qr.models.Qr;
import com.major.qr.viewmodels.QrLinkViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    public final static String NOT_ACCESSED_YET = "Not Accessed Yet";
    private static final String TAG = HomeFragment.class.getSimpleName();
    QrLinkViewModel viewModel;
    FragmentHomeBinding binding;
    QrDisplayAdapter qrDisplayAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(requireActivity()).get(QrLinkViewModel.class);

        viewModel.getQrLink().observe(requireActivity(), jsonArray -> {
            ArrayList<Qr> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); ++i) {
                try {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Qr qr = new Qr();
                    qr.setQrId(object.get("id").toString());
                    qr.setSessionName(object.getString("sessionName"));
                    qr.setToken(object.getString("token"));
                    if (object.has("lastSeen")) qr.setLastSeen(object.getString("lastSeen"));
                    else qr.setLastSeen(NOT_ACCESSED_YET);
                    qr.setSessionValidTime(object.getString("sessionValidTime"));
                    list.add(qr);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            qrDisplayAdapter = new QrDisplayAdapter(getContext(), list, viewModel);
            binding.recyclerView.setAdapter(qrDisplayAdapter);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.progressCircular.setVisibility(View.GONE);
            if (list.size() == 0) binding.emptyView.setVisibility(View.VISIBLE);
        });

        return binding.getRoot();
    }
}