package com.major.qr.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.major.qr.models.Attendee
import com.major.qr.ui.LoginActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AttendeesViewModel : ViewModel() {
    val TAG = AttendeesViewModel::class.java.simpleName
    var list: MutableLiveData<ArrayList<Attendee>>? = null

    /**
     * Get Attendees
     * @param Id Attendance Id
     * @return List of Attendees
     */
    fun getAttendees(Id: String): MutableLiveData<ArrayList<Attendee>> {
        if (list == null) {
            Log.d(TAG, "list = " + null)
            list = MutableLiveData()
        }
        loadAttendeesList(Id)
        return list as MutableLiveData<ArrayList<Attendee>>
    }

    private fun loadAttendeesList(id: String) {
        val url = AttendanceViewModel.ATTENDANCE_API_URL + "get?attendanceId=" + id
        val request: StringRequest =
            object : StringRequest(Method.GET, url, Response.Listener { response: String ->
                Log.d(TAG, "url = $url")
                Log.d(TAG, "success! response: $response")
                val attendeesArrayList = ArrayList<Attendee>()
                try {
                    val jsonArray = JSONArray(response)
                    Log.d(TAG, "jsonArray = $jsonArray")
                    for (i in 0 until jsonArray.length()) {
                        val `object` = jsonArray.getJSONObject(i)
                        Log.d(TAG, "loadAttendanceList: $`object`")
                        attendeesArrayList.add(
                            Attendee(
                                `object`.getString("addedDateTime"),
                                `object`.getString("displayName"),
                                `object`.getString("attendersId"),
                                `object`.getString("email"),
                                `object`.getString("recordId")
                            )
                        )
                    }
                    list!!.postValue(attendeesArrayList)
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
            }, Response.ErrorListener { error: VolleyError -> Log.e(TAG, "error: $error") }) {
                override fun getHeaders(): HashMap<String?, String?> {
                    return object : HashMap<String?, String?>() {
                        init {
                            put("accept", "*/*")
                            put("Authorization", LoginActivity.ACCESS_TOKEN)
                        }
                    }
                }
            }
        LoginActivity.requestQueue?.add(request)
    }

    fun removeAttendee(responseId: String?, attendanceId: String): MutableLiveData<JSONObject> {
        val res = MutableLiveData<JSONObject>()
        val url = AttendanceViewModel.ATTENDANCE_API_URL + String.format(
            "remove?attendanceId=%1\$s&recordId=%2\$s",
            attendanceId, responseId
        )
        Log.d(TAG, "url = $url")
        val request: JsonObjectRequest = object :
            JsonObjectRequest(Method.DELETE, url, null, Response.Listener { response: JSONObject ->
                Log.d(TAG, "success! response: $response")
                getAttendees(attendanceId)
                res.postValue(response)
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
        return res
    }
}