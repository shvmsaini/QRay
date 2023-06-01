package com.major.qr.viewmodels;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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

public class CreateAttendanceViewModel extends AndroidViewModel {
    public static final String TAG = CreateAttendanceViewModel.class.getSimpleName();
    MutableLiveData<ArrayList<Attendance>> list;

    public CreateAttendanceViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<ArrayList<Attendance>> getAttendances(){
        if(list == null){
            list = new MutableLiveData<>();
            loadAttendanceList();
        }
        return list;
    }
    private void loadAttendanceList(){
        final String url = LoginActivity.URL + "/attendance/get";
        RequestQueue queue = Volley.newRequestQueue(getApplication());
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Log.d(TAG, "response: " + response);
            ArrayList<Attendance> attendanceArrayList = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(response);
                Log.d(TAG, "jsonArray = " + jsonArray);
                for(int i = 0; i < jsonArray.length(); ++i){
                    JSONObject object = jsonArray.getJSONObject(i);
                    Log.d(TAG, "loadAttendanceList: " + object);
                    Attendance attendance = new Attendance();
                    if(!object.has("name")) continue;
                    attendance.setName(object.getString("name"));
                    attendance.setId(object.getString("id"));
                    attendance.setTotalAttenders(object.getString("totalAttenders"));
                    attendance.setCreationDate(object.getString("creationDate"));
                    attendanceArrayList.add(attendance);
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
}
