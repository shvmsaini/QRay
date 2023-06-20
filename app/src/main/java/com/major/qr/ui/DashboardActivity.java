package com.major.qr.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.major.qr.R;
import com.major.qr.databinding.ActivityDashboardBinding;
import com.major.qr.utils.CaptureActivityPortrait;
import com.major.qr.viewmodels.AttendanceViewModel;

public class DashboardActivity extends AppCompatActivity {
    public static final String INSTANCE = "http://qray.s3-website.ap-south-1.amazonaws.com/access/";
    public final String TAG = DashboardActivity.class.getSimpleName();
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
                transaction.replace(R.id.fragment, new ProfileFragment());
            } else if (item.getItemId() == R.id.home) {
                transaction.replace(R.id.fragment, new HomeFragment());
            } else if (item.getItemId() == R.id.attendance) {
                transaction.replace(R.id.fragment, new AttendanceFragment());
            } else if (item.getItemId() == R.id.documents) {
                transaction.replace(R.id.fragment, new DocumentFragment());
            } else if (item.getItemId() == R.id.scan) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                intentIntegrator.setPrompt("Scan QR for marking attendance");
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setCaptureActivity(CaptureActivityPortrait.class);
                intentIntegrator.initiateScan();
            }
            if (item.getItemId() != R.id.scan) {
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
                Log.d(TAG, "qrResponse: " + qrResponse);
                if (qrResponse.length() > 50) {
                    final String url = INSTANCE + qrResponse;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    this.startActivity(browserIntent);
                } else {
                    final String uid = LoginActivity.USERID;
                    final String attendanceId = qrResponse;
                    viewModel.markAttendance(uid, attendanceId).observe(this, s -> {
                        Toast.makeText(this, "Marked Successfully", Toast.LENGTH_LONG).show();
                    });
//                    try {
//                        JSONObject jsonObject = new JSONObject(qrResponse);
//                        final String uid = jsonObject.get("uid").toString();
//                        final String attendanceId = jsonObject.get("attendanceId").toString();
//                        viewModel.markAttendance(uid, attendanceId).observe(this, s -> {
//                            Toast.makeText(this, "Marked Successfully", Toast.LENGTH_LONG).show();
//                        });
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
                }
                Toast.makeText(this, "Found!", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
