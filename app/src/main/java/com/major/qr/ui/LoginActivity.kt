package com.major.qr.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.androidnetworking.AndroidNetworking
import com.major.qr.databinding.ActivityLoginBinding
import com.major.qr.ui.DashboardActivity
import com.major.qr.viewmodels.LoginViewModel
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName
    var binding: ActivityLoginBinding? = null
    var loginViewModel: LoginViewModel? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        // Volley
        requestQueue = Volley.newRequestQueue(application)
        AndroidNetworking.initialize(applicationContext)
        AndroidNetworking.enableLogging()
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        val preferences = getSharedPreferences(packageName, MODE_PRIVATE)

        // For signup activity link
        val signupSpan = SpannableString(binding!!.signup.text.toString())
        signupSpan.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                val signupIntent = Intent(
                    this@LoginActivity,
                    SignupActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(signupIntent)
            }
        }, 23, 29, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        binding!!.signup.text = signupSpan
        binding!!.signup.movementMethod = LinkMovementMethod.getInstance()
        binding!!.loginButton.setOnClickListener { view: View? ->
            val email = binding!!.emailForLogin.text.toString().trim { it <= ' ' }
            val password = binding!!.passwordForLogin.text.toString()
            if (email.length == 0) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length == 0) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val editor = preferences.edit()
            loginViewModel!!.getLoginData(email, password)
                .observe(this, Observer<JSONObject?> { jsonObject: JSONObject? ->
                    if (jsonObject == null) {
                        Toast.makeText(this, "Unable to login!", Toast.LENGTH_SHORT).show()
                        return@Observer
                    }
                    try {
                        if (jsonObject.has("uid") && !jsonObject.isNull("uid")) {
                            USERID = jsonObject["uid"] as String
                            NAME = jsonObject["displayName"] as String
                            EMAIL = jsonObject["email"] as String
                            ACCESS_TOKEN = jsonObject["accessToken"] as String
                        }
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    editor.putString("uid", USERID)
                    editor.putString("access_token", ACCESS_TOKEN)
                    editor.putString("name", NAME)
                    editor.putString("email", EMAIL)
                    editor.apply()
                    val intent = Intent(this, DashboardActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                    Toast.makeText(application, "Success! Logging in...", Toast.LENGTH_SHORT).show()
                })
        }

        // Checking if user is already logged with stored info
        if (preferences.getString("uid", "none") != "none") {
            USERID = preferences.getString("uid", "")
            ACCESS_TOKEN = preferences.getString("access_token", "")
            NAME = preferences.getString("name", "")
            EMAIL = preferences.getString("email", "")
            Log.d(TAG, "ACCESS_TOKEN = " + ACCESS_TOKEN)
            Log.d(TAG, "USERID = " + USERID)
            Log.d(TAG, "NAME = " + NAME)
            Log.d(TAG, "EMAIL = " + EMAIL)
            val intent = Intent(this, DashboardActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        const val URL = "http://43.204.29.197:8080/api"
        var NAME: String? = null
        var EMAIL: String? = null
        var ACCESS_TOKEN: String? = null
        var USERID: String? = null
        @JvmField
        var requestQueue: RequestQueue? = null
    }
}