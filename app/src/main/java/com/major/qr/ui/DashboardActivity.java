package com.major.qr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.major.qr.R;
import com.major.qr.databinding.ActivityDashboardBinding;
import com.major.qr.utils.CaptureActivityPortrait;
import com.major.qr.viewmodels.AttendanceViewModel;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardActivity extends AppCompatActivity {
    public static final String TAG = DashboardActivity.class.getSimpleName();
    AttendanceViewModel viewModel;
    ActivityDashboardBinding binding;

    String qrResponse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(AttendanceViewModel.class);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.navigation.setOnItemSelectedListener(item -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (item.getItemId() == R.id.profile) {
                Fragment newFragment = new ProfileFragment();
                transaction.replace(R.id.fragment, newFragment);
            } else if (item.getItemId() == R.id.home) {
                Fragment newFragment = new HomeFragment();
                transaction.replace(R.id.fragment, newFragment);
            } else if (item.getItemId() == R.id.attendance) {
                Fragment newFragment = new AttendanceFragment();
                transaction.replace(R.id.fragment, newFragment);
            } else if (item.getItemId() == R.id.documents) {
                Fragment newFragment = new DocumentFragment();
                transaction.replace(R.id.fragment, newFragment);
            } else if (item.getItemId() == R.id.scan) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                intentIntegrator.setPrompt("Scan QR for marking attendance");
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setCaptureActivity(CaptureActivityPortrait.class);
                intentIntegrator.initiateScan();
                if (qrResponse == null) {
                    Toast.makeText(this, "QR is null!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                Log.d(TAG, "AttendanceId: " + qrResponse);
                try {
                    JSONObject jsonObject = new JSONObject(qrResponse);
                    final String uid = jsonObject.get("uid").toString();
                    final String attendanceId = jsonObject.get("attendanceId").toString();
                    viewModel.markAttendance(uid, attendanceId).observe(this, s -> {
                        Toast.makeText(this, "Marked Successfully", Toast.LENGTH_LONG).show();
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            if(item.getItemId() != R.id.scan){
                transaction.commit();
                item.setChecked(true);
                item.setEnabled(true);
                return true;
            }
            return false;
        });

        binding.navigation.setSelectedItemId(R.id.home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                qrResponse = intentResult.getContents();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
