package com.major.qr.viewmodels;

import static com.major.qr.ui.LoginActivity.URL;
import static com.major.qr.ui.LoginActivity.requestQueue;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.major.qr.models.User;
import com.major.qr.ui.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ProfileViewModel extends ViewModel {
    public final String TAG = ProfileViewModel.class.getSimpleName();

    MutableLiveData<User> userDetails = null;

    public MutableLiveData<User> getUserDetails() {
        if (userDetails == null) {
            Log.d(TAG, "getUserDetails: Getting Details");
            userDetails = new MutableLiveData<>();
            final String url = URL + "/user/userDetails";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                User user = new User();
                try {
                    user.setCountry(response.getString("country"));
                    user.setFirstName(response.getString("firstName"));
                    user.setLastName(response.getString("lastName"));
                    user.setPhoneNumber(response.getString("phoneNumber"));
                    user.setState(response.getString("state"));
                    user.setEmail(response.getString("email"));
                    Log.d(TAG, "getUserDetails: " + user);
                    userDetails.postValue(user);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }, error -> Log.d(TAG, "getUserDetails: " + error)) {
                @Override
                public Map<String, String> getHeaders() {
                    return new HashMap<String, String>() {{
                        put("Authorization", LoginActivity.ACCESS_TOKEN);
                    }};
                }
            };
            requestQueue.add(request);
        }
        return userDetails;
    }

    public MutableLiveData<Boolean> updateUserDetails(User user) {
        MutableLiveData<Boolean> isFailed = new MutableLiveData<>();
        Log.d(TAG, "updateUserDetails: " + user);
        final String url = URL + "/user/updateUserDetails";
        JSONObject params = new JSONObject() {{
            try {
                put("country", user.getCountry());
                put("firstName", user.getFirstName());
                put("lastName", user.getLastName());
                put("phoneNumber", user.getPhoneNumber());
                put("state", user.getState());
                put("email", user.getEmail());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }};
        StringRequest request = new StringRequest(Request.Method.PUT, url, response -> {
            Log.d(TAG, "response = " + response);
            isFailed.postValue(Boolean.FALSE);
        }, error -> {
            Log.d(TAG, "getUserDetails: " + error);
            isFailed.postValue(null);
        }) {
            @Override
            public byte[] getBody() {
                return params.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<String, String>() {{
                    put("accept", "*/*");
                    put("Content-Type", "application/json");
                    put("Authorization", LoginActivity.ACCESS_TOKEN);
                }};
            }
        };
        requestQueue.add(request);
        return isFailed;
    }

}
