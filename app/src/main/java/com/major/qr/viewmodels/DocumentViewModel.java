package com.major.qr.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.major.qr.ui.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author shvmsaini
 */
public class DocumentViewModel extends AndroidViewModel {
    private static final String TAG = DocumentViewModel.class.getSimpleName();

    public DocumentViewModel(Application application) {
        super(application);
    }

    /**
     * Used to upload doc to database
     *
     * @param file File to upload
     */
    public void uploadDoc(File file) {
        final String URL = LoginActivity.URL + "/documents/upload";
        AndroidNetworking.upload(URL)
                .addHeaders("Authorization", LoginActivity.ACCESS_TOKEN)
                .addMultipartFile("document", file)
                .addMultipartParameter("documentType", "image")
                .setTag("uploadingFile")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener((bytesUploaded, totalBytes) -> Log.d(TAG, "" + bytesUploaded))
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: " + response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError);
                    }
                });
//        VolleyMultipartRequest multiPartRequest = new VolleyMultipartRequest(Request.Method.POST, URL, response -> {
//            Log.d("upload", response.toString());
////            try {
////                JSONObject jsonObject = new JSONObject(response);
////                Log.d(TAG, jsonObject.toString());
////            } catch (JSONException e) {
////                e.printStackTrace();
////            }
//        }, error -> {
//            Log.d(TAG, "uploadDoc: No internet Connection");
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                //Params
//                return params;
//            }
//
//            @Override
//            protected Map<String, DataPart> getByteData() {
//                Map<String, DataPart> params = new HashMap<>();
//                String pdfName = System.currentTimeMillis() + ".jpg";
//                params.put("jpg", new DataPart(fileLink, getFileData(fileLink)));
//                return params;
//            }
//        };

//        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplication());
//        requestQueue.add(multiPartRequest);
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
//            Log.d("upload", response);
//            try {
//                JSONObject jsonObject = new JSONObject(response);
//                Log.d(TAG, jsonObject.toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }, error -> {
//            Log.d(TAG, "uploadDoc: No internet Connection");
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                return new HashMap<String, String>() {{
//                    put("documentType", "image");
//                    put("document", fileLink);
//                }};
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                return new HashMap<String, String>() {{
//                    put("accept", "*/*");
//                    put("Authorization", LoginActivity.ACCESS_TOKEN);
//                    put("Content-Type", "multipart/form-data");
//                }};
//            }
//        };
//        {
//            int socketTimeout = 30000;
//            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//            stringRequest.setRetryPolicy(policy);
//            RequestQueue requestQueue = Volley.newRequestQueue(this.getApplication());
//            requestQueue.add(stringRequest);
//        }
    }

    /**
     * Used to update the doc in database
     *
     * @param fileLink link to the doc
     */
    public void updateDoc(String fileLink) {

    }

    /**
     * Retrieving document from database
     *
     * @return Document Link
     */
    public String getDoc() {
        String docLink = "";
        StringRequest request = new StringRequest(Request.Method.GET, LoginActivity.URL, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
            } catch (JSONException e) {

            }
            Log.d(TAG, response);
        }
                , error -> {
            Log.d(TAG, error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<>();
                //  params.put("image", image);
                return params;
            }
        };
        return docLink;
    }

    private byte[] getFileData(String file) {
        int size = (int) file.length();
//        int size = (int) pdf.length();
        byte[] bytes = new byte[size];
        byte[] tmpBuff = new byte[size];

        try (FileInputStream inputStream = new FileInputStream(file)) {
            int read = inputStream.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = inputStream.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }
}
