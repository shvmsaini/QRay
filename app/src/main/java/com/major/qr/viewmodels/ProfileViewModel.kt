package com.major.qr.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.major.qr.models.User
import com.major.qr.ui.LoginActivity
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class ProfileViewModel : ViewModel() {
    val TAG = ProfileViewModel::class.java.simpleName
    var userDetails: MutableLiveData<User>? = null
    fun userDetails(): MutableLiveData<User> {
        if (userDetails == null) {
            Log.d(TAG, "getUserDetails: Getting Details")
            userDetails = MutableLiveData()
            val url = LoginActivity.URL + "/user/userDetails"
            val request: JsonObjectRequest = object : JsonObjectRequest(
                Method.GET,
                url,
                null,
                Response.Listener { response: JSONObject ->
                    val user = User()
                    try {
                        user.country = response.getString("country")
                        user.firstName = response.getString("firstName")
                        user.lastName = response.getString("lastName")
                        user.phoneNumber = response.getString("phoneNumber")
                        user.state = response.getString("state")
                        user.email = response.getString("email")
                        Log.d(TAG, "getUserDetails: $user")
                        userDetails!!.postValue(user)
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    Log.d(
                        TAG,
                        "getUserDetails: $error"
                    )
                }) {
                override fun getHeaders(): HashMap<String?, String?> {
                    return object : HashMap<String?, String?>() {
                        init {
                            put("Authorization", LoginActivity.ACCESS_TOKEN)
                        }
                    }
                }
            }
            LoginActivity.requestQueue?.add(request)
        }
        return userDetails!!
    }

    fun updateUserDetails(user: User): MutableLiveData<Boolean?> {
        val isFailed = MutableLiveData<Boolean?>()
        Log.d(TAG, "updateUserDetails: $user")
        val url = LoginActivity.URL + "/user/updateUserDetails"
        val params: JSONObject = object : JSONObject() {
            init {
                try {
                    put("country", user.country)
                    put("firstName", user.firstName)
                    put("lastName", user.lastName)
                    put("phoneNumber", user.phoneNumber)
                    put("state", user.state)
                    put("email", user.email)
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
            }
        }
        val request: StringRequest =
            object : StringRequest(Method.PUT, url, Response.Listener { response: String ->
                Log.d(TAG, "response = $response")
                isFailed.postValue(java.lang.Boolean.FALSE)
            }, Response.ErrorListener { error: VolleyError ->
                Log.d(TAG, "getUserDetails: $error")
                isFailed.postValue(null)
            }) {
                override fun getBody(): ByteArray {
                    return params.toString().toByteArray(StandardCharsets.UTF_8)
                }

                override fun getHeaders(): HashMap<String?, String?> {
                    return object : HashMap<String?, String?>() {
                        init {
                            put("accept", "*/*")
                            put("Content-Type", "application/json")
                            put("Authorization", LoginActivity.ACCESS_TOKEN)
                        }
                    }
                }
            }
        LoginActivity.requestQueue?.add(request)
        return isFailed
    }
}