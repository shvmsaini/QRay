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
import com.major.qr.ui.LoginActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class QrLinkViewModel extends AndroidViewModel {
    public final String TAG = QrLinkViewModel.class.getSimpleName();

    public QrLinkViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<JSONObject> createQr(HashSet<String> docs, String sessionName,
                                                String type, String validTime) {
        MutableLiveData<JSONObject> mutableLiveData = new MutableLiveData<>();
        final String url = URL + String.format("/qrLink/create?sessionName=%1$s&type=%2$s&validTime=%3$s",
                sessionName, type, validTime);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, response -> {
            mutableLiveData.postValue(response);
            Log.d(TAG, "success! response: " + response);
        }, error -> {
            mutableLiveData.postValue(null);
            Log.e(TAG, "error: " + error.toString());
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<String, String>() {{
                    put("Content-Type", "application/json");
                    put("Authorization", LoginActivity.ACCESS_TOKEN);
                }};
            }

            @Override
            public byte[] getBody() {
                JSONArray jsonArray = new JSONArray();
                for (String docId : docs) {
                    jsonArray.put(docId);
                }
                return jsonArray.toString().getBytes(StandardCharsets.UTF_8);
            }
        };
        requestQueue.add(request);
        return mutableLiveData;
    }

    public MutableLiveData<JSONArray> getQrLink() {
        MutableLiveData<JSONArray> mutableLiveData = new MutableLiveData<>();
        final String url = URL + "/qrLink/get";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            mutableLiveData.postValue(response);
            Log.d(TAG, "success! response: " + response);
        }, error -> {
            mutableLiveData.postValue(null);
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
        return mutableLiveData;
    }

    public MutableLiveData<JSONObject> deleteQrLink(String qrId) {
        MutableLiveData<JSONObject> mutableLiveData = new MutableLiveData<>();
        final String url = URL + "/qrLink/delete?qrId=" + qrId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null, response -> {
            mutableLiveData.postValue(response);
            Log.d(TAG, "success! response: " + response);
        }, error -> {
            mutableLiveData.postValue(null);
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
        return mutableLiveData;
    }
}
