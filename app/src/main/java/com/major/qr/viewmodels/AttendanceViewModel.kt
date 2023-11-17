package com.major.qr.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.major.qr.models.Attendance
import com.major.qr.ui.LoginActivity
import com.major.qr.volley.VolleySingleton
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AttendanceViewModel : ViewModel() {
    val TAG = AttendanceViewModel::class.java.simpleName
    var list: MutableLiveData<ArrayList<Attendance>>? = null
    val attendances: MutableLiveData<ArrayList<Attendance>>
        get() {
            if (list == null) list = MutableLiveData()
            loadAttendanceList()
            return list as MutableLiveData<ArrayList<Attendance>>
        }

    private fun loadAttendanceList() {
        val url = LoginActivity.URL + "/attendance/get"
        val request: JsonArrayRequest = object :
            JsonArrayRequest(Method.GET, url, null, Response.Listener { jsonArray: JSONArray ->
                Log.d(TAG, "response: $jsonArray")
                val attendanceArrayList = ArrayList<Attendance>()
                try {
                    for (i in 0 until jsonArray.length()) {
                        val `object` = jsonArray.getJSONObject(i)
                        Log.d(TAG, "loadAttendanceList: $`object`")
                        if (!`object`.has("name")) continue
                        attendanceArrayList.add(
                            Attendance(
                                `object`.getString("id"),
                                `object`.getString("name"),
                                `object`.getString("totalAttenders"),
                                `object`.getString("creationDate")
                            )
                        )
                    }
                    list!!.postValue(attendanceArrayList)
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
            }, Response.ErrorListener { error: VolleyError -> Log.e(TAG, "error: $error") }) {
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

    fun markAttendance(uid: String, attendanceId: String?): MutableLiveData<String?> {
        val mutableLiveData = MutableLiveData<String?>()
        val params: JSONObject = object : JSONObject() {
            init {
                try {
                    put("attendanceId", attendanceId)
                    put("attendersId", LoginActivity.USERID)
                    put("displayName", LoginActivity.NAME)
                    put("email", LoginActivity.EMAIL)
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
            }
        }
        val url = LoginActivity.URL + "/attendance/mark?uid=" + uid
        Log.d(TAG, "markAttendance: $url")
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, params,
            Response.Listener<JSONObject> { response: JSONObject ->
                mutableLiveData.postValue(response.toString())
                Log.d(TAG, response.toString())
            },
            Response.ErrorListener { error: VolleyError? ->
                mutableLiveData.postValue(null)
                Log.d(TAG, "uploadDoc: No internet Connection")
            }) {
            override fun getHeaders(): HashMap<String?, String?> {
                return object : HashMap<String?, String?>() {
                    init {
                        put("accept", "*/*")
                        put("Authorization", LoginActivity.ACCESS_TOKEN)
                        put("Content-Type", "application/json")
                    }
                }
            }
        }
        LoginActivity.requestQueue?.add(jsonObjectRequest.setRetryPolicy(VolleySingleton.getRetryPolicy()))
        return mutableLiveData
    }

    fun createAttendance(name: String): MutableLiveData<JSONObject> {
        val res = MutableLiveData<JSONObject>()
        val url = ATTENDANCE_API_URL + "create?name=" + name
        Log.d(TAG, "createAttendance: $url")
        val request: JsonObjectRequest = object : JsonObjectRequest(Method.GET,
            url,
            null,
            Response.Listener { response: JSONObject ->
                loadAttendanceList()
                res.postValue(response)
            },
            Response.ErrorListener { error: VolleyError? ->
                Log.d(
                    TAG,
                    "uploadDoc: No internet Connection"
                )
            }) {
            override fun getHeaders(): HashMap<String?, String?> {
                return object : HashMap<String?, String?>() {
                    init {
                        put("accept", "*/*")
                        put("Authorization", LoginActivity.ACCESS_TOKEN)
                    }
                }
            }
        }
        LoginActivity.requestQueue?.add(request.setRetryPolicy(VolleySingleton.getRetryPolicy()))
        return res
    }

    fun deleteAttendance(attendanceId: String): MutableLiveData<JSONObject?> {
        val liveData = MutableLiveData<JSONObject?>()
        val url = LoginActivity.URL + "/attendance/delete?attendanceId=" + attendanceId
        val request: JsonObjectRequest = object : JsonObjectRequest(Method.DELETE, url, null,
            Response.Listener { response: JSONObject? ->
                loadAttendanceList()
                liveData.postValue(response)
            }, Response.ErrorListener { error: VolleyError ->
                liveData.postValue(null)
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
        return liveData
    }

    companion object {
        const val ATTENDANCE_API_URL = LoginActivity.URL + "/attendance/"
    }
}