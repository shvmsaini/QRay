package com.major.qr.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.major.qr.R;
import com.major.qr.databinding.ActivityDashboardBinding;
import com.major.qr.utils.CaptureActivityPortrait;
import com.major.qr.viewmodels.AttendanceViewModel;
import com.major.qr.viewmodels.DocumentViewModel;
import com.major.qr.viewmodels.ProfileViewModel;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardActivity extends AppCompatActivity {
    public static final String INSTANCE = "http://qray.s3-website.ap-south-1.amazonaws.com/access/";
    public final String TAG = DashboardActivity.class.getSimpleName();
    AttendanceViewModel viewModel;
    ActivityDashboardBinding binding;
    JSONObject qrResponse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(AttendanceViewModel.class);

        ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        profileViewModel.getUserDetails();

        AttendanceViewModel attendanceViewModel = new ViewModelProvider(this).get(AttendanceViewModel.class);
        attendanceViewModel.getAttendances();

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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    qrResponse = new JSONObject(intentResult.getContents());
                } catch (JSONException e) {
                    Log.d(TAG, "onActivityResult: Not a Json");
                    final String url = INSTANCE + intentResult.getContents();
                    this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return;
                }
                Log.d(TAG, "qrResponse: " + qrResponse);
                final String uid;
                final String attendanceId;
                try {
                    attendanceId = qrResponse.getString("ID");
                    uid = qrResponse.getString("UID");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                viewModel.markAttendance(uid, attendanceId).observe(this, response -> {
                    if (response == null)
                        Toast.makeText(this, "Unable to mark attendance try again!", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(this, "Marked Successfully", Toast.LENGTH_SHORT).show();
                });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
