package com.major.qr.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.major.qr.ui.LoginActivity
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class LoginViewModel : ViewModel() {
    val TAG = LoginViewModel::class.java.simpleName
    fun getLoginData(email: String?, password: String?): MutableLiveData<JSONObject?> {
        val loginData = MutableLiveData<JSONObject?>()
        val url = LoginActivity.URL + "/user/login"
        val request: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener { response: String ->
                try {
                    loginData.postValue(JSONObject(response))
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                Log.d(TAG, "success! response: $response")
            }, Response.ErrorListener { error: VolleyError ->
                loginData.postValue(null)
                Log.e(TAG, "error: $error")
            }) {
                override fun getParams(): HashMap<String?, String?> {
                    return object : HashMap<String?, String?>() {
                        init {
                            put("email", email)
                            put("password", password)
                        }
                    }
                }

                override fun getHeaders(): HashMap<String?, String?> {
                    return object : HashMap<String?, String?>() {
                        init {
                            put("Content-Type", "application/json")
                        }
                    }
                }

                override fun getBody(): ByteArray {
                    return object : JSONObject() {
                        init {
                            try {
                                put("email", email)
                                put("password", password)
                            } catch (e: JSONException) {
                                throw RuntimeException(e)
                            }
                        }
                    }.toString().toByteArray(StandardCharsets.UTF_8)
                }
            }
        LoginActivity.requestQueue?.add(request)
        return loginData
    }
}