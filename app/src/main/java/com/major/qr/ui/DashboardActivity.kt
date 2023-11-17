package com.major.qr.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.zxing.integration.android.IntentIntegrator
import com.major.qr.R
import com.major.qr.databinding.ActivityDashboardBinding
import com.major.qr.utils.CaptureActivityPortrait
import com.major.qr.viewmodels.AttendanceViewModel
import com.major.qr.viewmodels.ProfileViewModel
import org.json.JSONException
import org.json.JSONObject

class DashboardActivity : AppCompatActivity() {
    val TAG = DashboardActivity::class.java.simpleName
    var viewModel: AttendanceViewModel? = null
    var binding: ActivityDashboardBinding? = null
    var qrResponse: JSONObject? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AttendanceViewModel::class.java)
        val profileViewModel = ViewModelProvider(this).get(
            ProfileViewModel::class.java
        )
        profileViewModel.userDetails()
        val attendanceViewModel = ViewModelProvider(this).get(
            AttendanceViewModel::class.java
        )
        attendanceViewModel.attendances
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.navigation.setOnItemSelectedListener { item: MenuItem ->
            val transaction = supportFragmentManager.beginTransaction()
            if (item.itemId == R.id.profile) {
                transaction.replace(R.id.fragment, ProfileFragment())
            } else if (item.itemId == R.id.home) {
                transaction.replace(R.id.fragment, HomeFragment())
            } else if (item.itemId == R.id.attendance) {
                transaction.replace(R.id.fragment, AttendanceFragment())
            } else if (item.itemId == R.id.documents) {
                transaction.replace(R.id.fragment, DocumentFragment())
            } else if (item.itemId == R.id.scan) {
                val intentIntegrator = IntentIntegrator(this)
                intentIntegrator.setPrompt("Scan QR for marking attendance")
                intentIntegrator.setOrientationLocked(true)
                intentIntegrator.captureActivity = CaptureActivityPortrait::class.java
                intentIntegrator.initiateScan()
            }
            if (item.itemId != R.id.scan) {
                transaction.commit()
                item.isChecked = true
                item.isEnabled = true
                return@setOnItemSelectedListener true
            }
            false
        }
        binding!!.navigation.selectedItemId = R.id.home
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (intentResult != null) {
            if (intentResult.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                qrResponse = try {
                    JSONObject(intentResult.contents)
                } catch (e: JSONException) {
                    Log.d(TAG, "onActivityResult: Not a Json")
                    val url = INSTANCE + intentResult.contents
                    this.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    return
                }
                Log.d(TAG, "qrResponse: $qrResponse")
                val uid: String
                val attendanceId: String
                try {
                    attendanceId = qrResponse!!.getString("ID")
                    uid = qrResponse!!.getString("UID")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                viewModel!!.markAttendance(uid, attendanceId).observe(
                    this,
                    Observer { response: String? ->
                        if (response == null) Toast.makeText(
                            this,
                            "Unable to mark attendance try again!",
                            Toast.LENGTH_SHORT
                        ).show() else Toast.makeText(
                            this,
                            "Marked Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        const val INSTANCE = "http://qray.s3-website.ap-south-1.amazonaws.com/access/"
    }
}