package com.major.qr.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.major.qr.pojo.Attendees;
import com.major.qr.ui.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttendeesViewModel extends AndroidViewModel {
    public static final String TAG = AttendeesViewModel.class.getSimpleName();
    MutableLiveData<ArrayList<Attendees>> list;

    public AttendeesViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<ArrayList<Attendees>> getAttendees(String Id) {
        if (list == null) {
            list = new MutableLiveData<>();
            loadAttendeesList(Id);
        }
        return list;
    }

    private void loadAttendeesList(String id) {
        final String url = LoginActivity.URL + "/attendance/get?attendanceId=" + id;
        RequestQueue queue = Volley.newRequestQueue(getApplication());
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Log.d("HttpClient", "success! response: " + response);
            ArrayList<Attendees> attendeesArrayList = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(response);
                Log.d(TAG, "jsonArray = " + jsonArray);
                for(int i = 0; i < jsonArray.length(); ++i){
                    JSONObject object = jsonArray.getJSONObject(i);
                    Log.d(TAG, "loadAttendanceList: " + object);
                    Attendees attendee = new Attendees();
                    attendee.setDisplayName(object.getString("displayName"));
                    attendee.setAddedDateTime(object.getString("addedDateTime"));
                    attendee.setEmail(object.getString("email"));
                    attendeesArrayList.add(attendee);
                }
                list.postValue(attendeesArrayList);
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
}
