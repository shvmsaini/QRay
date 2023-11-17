package com.major.qr.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.major.qr.ui.LoginActivity
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class QrLinkViewModel : ViewModel() {
    val TAG = QrLinkViewModel::class.java.simpleName
    var mutableLiveData: MutableLiveData<JSONArray?>? = null
    fun createQr(
        docs: HashSet<String?>, sessionName: String?,
        type: String?, validTime: String?
    ): MutableLiveData<JSONObject?> {
        val mutableLiveData = MutableLiveData<JSONObject?>()
        val url = LoginActivity.URL + String.format(
            "/qrLink/create?sessionName=%1\$s&type=%2\$s&validTime=%3\$s",
            sessionName, type, validTime
        )
        val request: JsonObjectRequest = object :
            JsonObjectRequest(Method.POST, url, null, Response.Listener { response: JSONObject ->
                qrLink
                mutableLiveData.postValue(response)
                Log.d(TAG, "success! response: $response")
            }, Response.ErrorListener { error: VolleyError ->
                mutableLiveData.postValue(null)
                Log.e(TAG, "error: $error")
            }) {
            override fun getHeaders(): HashMap<String?, String?> {
                return object : HashMap<String?, String?>() {
                    init {
                        put("Content-Type", "application/json")
                        put("Authorization", LoginActivity.ACCESS_TOKEN)
                    }
                }
            }

            override fun getBody(): ByteArray {
                val jsonArray = JSONArray()
                for (docId in docs) {
                    jsonArray.put(docId)
                }
                return jsonArray.toString().toByteArray(StandardCharsets.UTF_8)
            }
        }
        LoginActivity.requestQueue?.add(request)
        return mutableLiveData
    }

    val qrLink: MutableLiveData<JSONArray?>
        get() {
            if (mutableLiveData == null) {
                mutableLiveData = MutableLiveData()
            }
            val url = LoginActivity.URL + "/qrLink/get"
            val request: JsonArrayRequest = object :
                JsonArrayRequest(Method.GET, url, null, Response.Listener { response: JSONArray ->
                    mutableLiveData!!.postValue(response)
                    Log.d(TAG, "success! response: $response")
                }, Response.ErrorListener { error: VolleyError ->
                    mutableLiveData!!.postValue(null)
                    Log.e(TAG, "error: $error")
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
            return mutableLiveData!!
        }

    fun deleteQrLink(qrId: String): MutableLiveData<JSONObject?> {
        val mutableLiveData = MutableLiveData<JSONObject?>()
        val url = LoginActivity.URL + "/qrLink/delete?qrId=" + qrId
        val request: JsonObjectRequest = object :
            JsonObjectRequest(Method.DELETE, url, null, Response.Listener { response: JSONObject ->
                qrLink
                mutableLiveData.postValue(response)
                Log.d(TAG, "success! response: $response")
            }, Response.ErrorListener { error: VolleyError ->
                mutableLiveData.postValue(null)
                Log.e(TAG, "error: $error")
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
        return mutableLiveData
    }
}