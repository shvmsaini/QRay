package com.major.qr.viewmodels;

import static com.major.qr.viewmodels.AttendanceViewModel.ATTENDANCE_API_URL;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.major.qr.models.Attendee;
import com.major.qr.ui.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttendeesViewModel extends AndroidViewModel {
    public final String TAG = AttendeesViewModel.class.getSimpleName();
    MutableLiveData<ArrayList<Attendee>> list;

    public AttendeesViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<ArrayList<Attendee>> getAttendees(String Id) {
        list = new MutableLiveData<>();
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
                    final String addedDateTime = LocalDateTime.parse(
                                    object.getString("addedDateTime"), DateTimeFormatter
                                            .ofPattern("yyyy-MM-dd HH:mm:ss"))
                            .format(DateTimeFormatter
                                    .ofPattern("yyyy MMM d, HH:mm:ss"));
                    attendeesArrayList.add(new Attendee(
                            addedDateTime,
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
                    put("Authorization", LoginActivity.ACCESS_TOKEN);
                }};
            }
        };
        LoginActivity.requestQueue.add(request);
    }

    public MutableLiveData<JSONObject> removeAttendee(String responseId, String attendanceId) {
        MutableLiveData<JSONObject> res = new MutableLiveData<>();
        final String url = ATTENDANCE_API_URL + String.format("remove?attendanceId=%1$s&recordId=%2$s",
                attendanceId, responseId);
        Log.d(TAG, "url = " + url);
        StringRequest request = new StringRequest(Request.Method.DELETE, url, response -> {
            Log.d(TAG, "success! response: " + response);
            try {
                res.postValue(new JSONObject(response));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> Log.e(TAG, "error: " + error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<String, String>() {{
                    put("accept", "*/*");
                    put("Authorization", LoginActivity.ACCESS_TOKEN);
                }};
            }
        };
        LoginActivity.requestQueue.add(request);
        return res;
    }
}
