package com.major.qr.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.major.qr.adapters.HomeDisplayAdapter;
import com.major.qr.databinding.HomeBinding;
import com.major.qr.pojo.Doc;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private HomeBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = HomeBinding.inflate(getLayoutInflater());
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false);
        Doc d = new Doc("HOLA", "AMIGO", "!");
        ArrayList<Doc> list = new ArrayList<Doc>(){{
            add(d);
            add(d);
            add(d);
            add(d);
        }};
        HomeDisplayAdapter homeDisplayAdapter = new HomeDisplayAdapter(this.getContext(), list);
        binding.recyclerView.setAdapter(homeDisplayAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.progressCircular.setVisibility(View.GONE);
        return binding.getRoot();
    }
}