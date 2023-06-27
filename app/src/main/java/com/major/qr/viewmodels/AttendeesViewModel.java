package com.major.qr.viewmodels;

import static com.major.qr.ui.LoginActivity.ACCESS_TOKEN;
import static com.major.qr.ui.LoginActivity.requestQueue;
import static com.major.qr.viewmodels.AttendanceViewModel.ATTENDANCE_API_URL;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.major.qr.models.Attendee;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttendeesViewModel extends ViewModel {
    public final String TAG = AttendeesViewModel.class.getSimpleName();
    MutableLiveData<ArrayList<Attendee>> list;

    /**
     * Get Attendees
      * @param Id Attendance Id
     * @return List of Attendees
     */
    public MutableLiveData<ArrayList<Attendee>> getAttendees(String Id) {
        if(list == null) {
            Log.d(TAG, "list = " + null);
            list = new MutableLiveData<>();
        }
        loadAttendeesList(Id);
        return list;
    }

    private void loadAttendeesList(String id) {
        final String url = ATTENDANCE_API_URL + "get?attendanceId=" + id;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Log.d(TAG, "url = " + url);
            Log.d(TAG, "success! response: " + response);
            ArrayList<Attendee> attendeesArrayList = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(response);
                Log.d(TAG, "jsonArray = " + jsonArray);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Log.d(TAG, "loadAttendanceList: " + object);

                    attendeesArrayList.add(new Attendee(
                             object.getString("addedDateTime"),
                            object.getString("displayName"),
                            object.getString("attendersId"),
                            object.getString("email"),
                            object.getString("recordId")));
                }
                list.postValue(attendeesArrayList);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> Log.e(TAG, "error: " + error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<String, String>() {{
                    put("accept", "*/*");
                    put("Authorization", ACCESS_TOKEN);
                }};
            }
        };
        requestQueue.add(request);
    }

    public MutableLiveData<JSONObject> removeAttendee(String responseId, String attendanceId) {
        MutableLiveData<JSONObject> res = new MutableLiveData<>();
        final String url = ATTENDANCE_API_URL + String.format("remove?attendanceId=%1$s&recordId=%2$s",
                attendanceId, responseId);
        Log.d(TAG, "url = " + url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null, response -> {
            Log.d(TAG, "success! response: " + response);
            getAttendees(attendanceId);
            res.postValue(response);
        }, error -> Log.e(TAG, "error: " + error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<String, String>() {{
                    put("Authorization", ACCESS_TOKEN);
                }};
            }
        };
        requestQueue.add(request);
        return res;
    }
}
