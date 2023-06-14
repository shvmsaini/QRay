package com.major.qr.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.major.qr.pojo.Doc;
import com.major.qr.ui.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shvmsaini
 */
public class DocumentViewModel extends AndroidViewModel {
    private static final String TAG = DocumentViewModel.class.getSimpleName();
    private static final String UPLOAD_MAPPING = "/documents/upload";
    private MutableLiveData<ArrayList<Doc>> docList;
    private HashMap<String, MutableLiveData<String>> documentReferencesMLD;

    public DocumentViewModel(Application application) {
        super(application);
        documentReferencesMLD = new HashMap<>();
    }

    /**
     * Used to upload doc to database
     *
     * @param file File to upload
     */
    public MutableLiveData<String> uploadDoc(File file) {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        final String URL = LoginActivity.URL + UPLOAD_MAPPING;
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
                        mutableLiveData.postValue(response.toString());
                        Log.d(TAG, "onResponse: " + response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError);
                    }
                });
        return mutableLiveData;
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
     * @param docReference reference id of doc
     * @param file         file to replace it with
     */
    public MutableLiveData<String> updateDoc(String docReference, File file) {
        MutableLiveData<String> updateDocResponse = new MutableLiveData<>();
        final String URL = LoginActivity.URL + "/documents/update/";
        AndroidNetworking.put(URL)
                .addHeaders("Authorization", LoginActivity.ACCESS_TOKEN)
                .addFileBody(file)
                .addBodyParameter("documentReference", docReference)
                .setTag("updatingFile")
                .setPriority(Priority.IMMEDIATE)
                .build()
//                .setUploadProgressListener((bytesUploaded, totalBytes) -> Log.d(TAG, "" + bytesUploaded))
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        updateDocResponse.postValue(response.toString());
                        Log.d(TAG, "onResponse: " + response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError);
                    }
                });
        return updateDocResponse;
    }

    /**
     * Retrievs all documents from database
     *
     * @return List of all docs uploaded by user
     */
    public MutableLiveData<ArrayList<Doc>> getDocs() {
        if (docList != null)
            return docList;
        docList = new MutableLiveData<>();
        final String url = LoginActivity.URL + "/documents/getDocuments";
        RequestQueue queue = Volley.newRequestQueue(getApplication());
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            ArrayList<Doc> docs = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(response);
                Log.d(TAG, "jsonArray = " + jsonArray);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Doc d = new Doc(
                            object.getString("documentType"),
                            object.getString("documentReference"),
                            object.getString("documentId"));
                    docs.add(d);
                }
                docList.postValue(docs);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> Log.d(TAG, error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<String, String>() {{
                    put("Authorization", LoginActivity.ACCESS_TOKEN);
                }};
            }
        };
        queue.add(request);
        return docList;
    }

    /**
     * Retrieving document from database
     *
     * @return Document Link
     */
    public MutableLiveData<String> getDocLink(String documentReference) {
        if (!documentReferencesMLD.containsKey(documentReference)) {
            documentReferencesMLD.put(documentReference, new MutableLiveData<>());
            Log.d(TAG, "getDocLink: ");
            final String url = LoginActivity.URL + "/documents/download/";
            RequestQueue queue = Volley.newRequestQueue(getApplication());
            StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                Log.d(TAG, response);
                documentReferencesMLD.get(documentReference).postValue(response);
            }, error -> Log.d(TAG, error.toString())) {
                @Override
                public Map<String, String> getHeaders() {
                    return new HashMap<String, String>() {{
                        put("Authorization", LoginActivity.ACCESS_TOKEN);
                        put("Content-Type", "application/json");
                    }};
                }

                @Override
                public byte[] getBody() {
                    return documentReference.getBytes(StandardCharsets.UTF_8);
                }
            };
            queue.add(request);
        }
        return documentReferencesMLD.get(documentReference);
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

    /**
     * Deletes Document
     *
     * @param documentId
     * @param docReference
     */
    public MutableLiveData<Object> deleteDoc(String documentId, String docReference) {
        MutableLiveData<Object> mutableLiveData = new MutableLiveData<>();
        final String url = LoginActivity.URL + "/documents/delete/" + "?documentId=" + documentId
                + "&documentReference=" + docReference;
        RequestQueue queue = Volley.newRequestQueue(getApplication());
        StringRequest request = new StringRequest(Request.Method.DELETE, url, response -> {
            Log.d(TAG, response);
            mutableLiveData.postValue(response);
        }, error -> Log.d(TAG, error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<String, String>() {{
                    put("Authorization", LoginActivity.ACCESS_TOKEN);
                    put("accept", "*/*");
                }};
            }
        };
        queue.add(request);
        return mutableLiveData;
    }
}
