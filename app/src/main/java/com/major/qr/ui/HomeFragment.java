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
        ArrayList<Doc> list = new ArrayList<>();
        Doc d = new Doc("HOLA", "AMIGO", "!", true);
        Doc d1 = new Doc("HOLA", "AMIGO", "!", true);
        Doc d2 = new Doc("HOLA", "AMIGO", "!", true);
        Doc d3 = new Doc("HOLA", "AMIGO", "!", true);
        Doc d4 = new Doc("HOLA", "AMIGO", "!", true);
        list.add(d);
        list.add(d1);
        list.add(d2);
        list.add(d3);
        list.add(d4);
        HomeDisplayAdapter homeDisplayAdapter = new HomeDisplayAdapter(this.getContext(), list);
        binding.recyclerView.setAdapter(homeDisplayAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.progressCircular.setVisibility(View.GONE);
        return binding.getRoot();
    }
}