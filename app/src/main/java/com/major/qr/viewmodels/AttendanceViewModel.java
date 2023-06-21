package com.major.qr.viewmodels;

import static com.major.qr.ui.LoginActivity.URL;
import static com.major.qr.ui.LoginActivity.requestQueue;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.major.qr.models.Attendance;
import com.major.qr.ui.LoginActivity;
import com.major.qr.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttendanceViewModel extends AndroidViewModel {
    public final static String ATTENDANCE_API_URL = URL + "/attendance/";
    public final String TAG = AttendanceViewModel.class.getSimpleName();
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
        final String url = URL + "/attendance/get";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, jsonArray -> {
            Log.d(TAG, "response: " + jsonArray.toString());
            ArrayList<Attendance> attendanceArrayList = new ArrayList<>();
            try {
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Log.d(TAG, "loadAttendanceList: " + object);
                    if (!object.has("name")) continue;
                    final String creationDate = LocalDateTime.parse(object.getString("creationDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).format(DateTimeFormatter.ofPattern("yyyy MMM d, KK:mm:ss"));
                    attendanceArrayList.add(new Attendance(object.getString("id"), object.getString("name"), object.getString("totalAttenders"), creationDate));
                }
                list.postValue(attendanceArrayList);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> Log.e(TAG, "error: " + error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<String, String>() {{
                    put("Authorization", LoginActivity.ACCESS_TOKEN);
                }};
            }
        };
        requestQueue.add(request);
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
        final String url = URL + "/attendance/mark?uid=" + uid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> Log.d(TAG, response.toString()),
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
        requestQueue.add(jsonObjectRequest.setRetryPolicy(VolleySingleton.getRetryPolicy()));
        return mutableLiveData;
    }

    public MutableLiveData<JSONObject> createAttendance() {
        MutableLiveData<JSONObject> res = new MutableLiveData<>();
        final String url = ATTENDANCE_API_URL + "create";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                res::postValue, error -> Log.d(TAG, "uploadDoc: No internet Connection")) {
            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<String, String>() {{
                    put("accept", "*/*");
                    put("Authorization", LoginActivity.ACCESS_TOKEN);
                }};
            }
        };
        requestQueue.add(request.setRetryPolicy(VolleySingleton.getRetryPolicy()));
        return res;
    }

    public MutableLiveData<JSONObject> deleteAttendance(String attendanceId) {
        MutableLiveData<JSONObject> liveData = new MutableLiveData<>();
        final String url = URL + "/attendance/delete?attendanceId=" + attendanceId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                liveData::postValue, error -> {
            liveData.postValue(null);
            Log.e(TAG, "error: " + error.toString());
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<String, String>() {{
                    put("Authorization", LoginActivity.ACCESS_TOKEN);
                }};
            }
        };
        requestQueue.add(request);
        return liveData;
    }
}
