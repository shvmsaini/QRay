package com.major.qr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.major.qr.R;
import com.major.qr.databinding.DashboardBinding;

public class Dashboard extends AppCompatActivity {
    private DashboardBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.navigation.setOnItemSelectedListener(item -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (item.getItemId() == R.id.profile) {
                Fragment newFragment = new ProfileFragment();
                transaction.replace(R.id.fragment, newFragment);
                transaction.commit();
                item.setChecked(true);
            } else if (item.getItemId() == R.id.home) {
                Fragment newFragment = new HomeFragment();
                transaction.replace(R.id.fragment, newFragment);
                transaction.commit();
                item.setChecked(true);
            } else {
                PopupMenu popupMenu = new PopupMenu(Dashboard.this, findViewById(R.id.more));
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.more_options, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    Intent i;
                    if (menuItem.getItemId() == R.id.attendance)
                        i = new Intent(Dashboard.this, AttendanceActivity.class);
                    else if (menuItem.getItemId() == R.id.documents)
                        i = new Intent(Dashboard.this, DocumentActivity.class);
                        // TODO: QR Code
                    else i = new Intent(Dashboard.this, DocumentActivity.class);
                    startActivity(i);
                    return false;
                });
            }
            return false;
        });

        binding.navigation.setSelectedItemId(R.id.home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_options, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }
}
