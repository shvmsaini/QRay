package com.major.qr.viewmodels;

import static com.major.qr.ui.LoginActivity.URL;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginViewModel extends AndroidViewModel {
    public final String TAG = LoginViewModel.class.getSimpleName();

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<JSONObject> getLoginData(String email, String password) {
        MutableLiveData<JSONObject> loginData = new MutableLiveData<>();
        final String url = URL + "/user/login";
        RequestQueue queue = Volley.newRequestQueue(getApplication());
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            try {
                loginData.postValue(new JSONObject(response));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            Log.d(TAG, "success! response: " + response);
        }, error -> {
            loginData.postValue(null);
            Log.e(TAG, "error: " + error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<String, String>() {{
                    put("email", email);
                    put("password", password);
                }};
            }

            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<String, String>() {{
                    put("Content-Type", "application/json");
                }};
            }

            @Override
            public byte[] getBody() {
                return new JSONObject() {{
                    try {
                        put("email", email);
                        put("password", password);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }}.toString().getBytes(StandardCharsets.UTF_8);
            }
        };
        queue.add(request);
        return loginData;
    }
}
