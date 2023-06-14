package com.major.qr.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.major.qr.pojo.Attendance;
import com.major.qr.ui.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttendanceViewModel extends AndroidViewModel {
    public static final String TAG = AttendanceViewModel.class.getSimpleName();
    MutableLiveData<ArrayList<Attendance>> list;

    public AttendanceViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<ArrayList<Attendance>> getAttendances() {
        if (list == null) {
            list = new MutableLiveData<>();
            loadAttendanceList();
        }
        return list;
    }

    private void loadAttendanceList() {
        final String url = LoginActivity.URL + "/attendance/get";
        RequestQueue queue = Volley.newRequestQueue(getApplication());
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Log.d(TAG, "response: " + response);
            ArrayList<Attendance> attendanceArrayList = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(response);
                Log.d(TAG, "jsonArray = " + jsonArray);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Log.d(TAG, "loadAttendanceList: " + object);
                    if (!object.has("name")) continue;
                    attendanceArrayList.add(new Attendance(
                            object.getString("id"),
                            object.getString("name"),
                            object.getString("totalAttenders"),
                            object.getString("creationDate")));
                }
                list.postValue(attendanceArrayList);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            Log.e("HttpClient", "error: " + error.toString());
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<String, String>() {{
                    put("Authorization", LoginActivity.ACCESS_TOKEN);
                }};
            }
        };
        queue.add(request);
    }

    public MutableLiveData<String> markAttendance(String uid, String attendanceId) {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        JSONObject params = new JSONObject() {{
            try {
                put("attendanceId", attendanceId);
                put("attendersId", LoginActivity.USERID);
                put("displayName", LoginActivity.NAME);
                put("email", LoginActivity.EMAIL);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }};
        final String url = LoginActivity.URL + "/attendance/mark?uid=" + uid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                params, response -> Log.d(TAG, response.toString()),
                error -> Log.d(TAG, "uploadDoc: No internet Connection")) {
            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<String, String>() {{
                    put("accept", "*/*");
                    put("Authorization", LoginActivity.ACCESS_TOKEN);
                    put("Content-Type", "application/json");
                }};
            }
        };
        {
            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            RequestQueue requestQueue = Volley.newRequestQueue(this.getApplication());
            requestQueue.add(jsonObjectRequest);
        }
        return mutableLiveData;
    }
}
