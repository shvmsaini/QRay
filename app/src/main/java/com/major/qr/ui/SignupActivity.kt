package com.major.qr.ui

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.major.qr.databinding.ActivitySignupBinding
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.Arrays
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName
    private var binding: ActivitySignupBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        // For log in link
        val signupSpan = SpannableString(binding!!.loginBack.text.toString())
        signupSpan.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                onBackPressed()
            }
        }, 25, 30, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        binding!!.loginBack.text = signupSpan
        binding!!.loginBack.movementMethod = LinkMovementMethod.getInstance()
        // Details
        binding!!.signupButton.setOnClickListener { v: View? ->
            Log.d(TAG, "Preparing Request")
            val name = binding!!.nameForSignup.text.toString()
            val lastName = binding!!.lastNameForSignup.text.toString()
            val email = binding!!.emailForSignup.text.toString()
            val password = binding!!.passwordForSignup.text.toString()
            val confirmPassword = binding!!.confirmPasswordForSignup.text.toString()
            val phoneNumber = binding!!.phoneNumber.text.toString()
            val country = binding!!.country.text.toString()
            if (!validate(
                    name,
                    email,
                    password,
                    confirmPassword,
                    phoneNumber,
                    country
                )
            ) return@setOnClickListener
            val url = LoginActivity.URL + "/user/register"
            val mp: JSONObject = object : JSONObject() {
                init {
                    try {
                        put("country", country)
                        put("email", email)
                        put("firstName", name)
                        put("lastName", lastName)
                        put("password", password)
                        put("phoneNumber", phoneNumber)
                        put("state", "delhi")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                }
            }
            Log.d(
                TAG,
                "Request body" + Arrays.toString(mp.toString().toByteArray(StandardCharsets.UTF_8))
            )
            val request: StringRequest = object :
                StringRequest(Method.POST, url, Response.Listener<String> { response: String ->
                    Log.d(TAG, "Response is: $response")
                    Toast.makeText(this, "Successfully registered!", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }, Response.ErrorListener { error: VolleyError ->
                    Toast.makeText(
                        this,
                        "Unable to register! Make sure you are entering" +
                                " an email address that is not already registered.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(TAG, "onErrorResponse: $error")
                }) {
                override fun getHeaders(): HashMap<String?, String?> {
                    return object : HashMap<String?, String?>() {
                        init {
                            put("accept", "*/*")
                            put("Content-Type", "application/json")
                        }
                    }
                }

                override fun getBody(): ByteArray {
                    return mp.toString().toByteArray(StandardCharsets.UTF_8)
                }
            }
            LoginActivity.requestQueue!!.add(request)
        }
    }

    fun validate(
        name: String, email: String, password: String, confirmPassword: String, phoneNumber: String,
        country: String
    ): Boolean {
        if (name.length == 0) {
            Toast.makeText(
                this, "Please enter your name.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (!validateEmail(email)) {
            Toast.makeText(
                this, "Please Enter valid email address.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (phoneNumber.length != 10) {
            Toast.makeText(
                this, "Please Enter valid phone number.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (password.length < 6) {
            Toast.makeText(
                this, "Password length should be at least 6!",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (confirmPassword != password) {
            Toast.makeText(
                this, "Password didn't match!",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (country.length == 0) {
            Toast.makeText(
                this, "Please enter you country.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    private fun validateEmail(email: String): Boolean {
        val matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email)
        return matcher.matches()
    }

    companion object {
        val VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
        )
    }
}