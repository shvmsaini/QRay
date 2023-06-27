package com.major.qr.viewmodels;

import static com.major.qr.ui.LoginActivity.URL;
import static com.major.qr.ui.LoginActivity.requestQueue;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.major.qr.models.Doc;
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
public class DocumentViewModel extends ViewModel {
    private static final String UPLOAD_MAPPING = "/documents/upload";
    private final String TAG = DocumentViewModel.class.getSimpleName();
    private MutableLiveData<ArrayList<Doc>> docList;
    private HashMap<String, MutableLiveData<String>> documentReferencesMLD;

    /**
     * Used to upload doc to database
     *
     * @param file File to upload
     */
    public MutableLiveData<String> uploadDoc(File file) {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        final String extension = file.getName().substring(file.getName().lastIndexOf('.') + 1);
        String type;
        if (extension.equals("pdf")) type = "pdf";
        else type = "image";
        final String URL = LoginActivity.URL + UPLOAD_MAPPING;
        AndroidNetworking.upload(URL)
                .addHeaders("Authorization", LoginActivity.ACCESS_TOKEN)
                .addMultipartFile("document", file)
                .addMultipartParameter("documentType", type)
                .setTag("uploadingFile")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener((bytesUploaded, totalBytes) ->
                        Log.d(TAG, "Bytes Uploaded" + bytesUploaded))
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        getDocs();
                        mutableLiveData.postValue(response.toString());
                        Log.d(TAG, "onResponse: " + response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError);
                    }
                });
        return mutableLiveData;
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
        if (docList == null) docList = new MutableLiveData<>();
        final String url = URL + "/documents/getDocuments";
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
        requestQueue.add(request);
        return docList;
    }

    /**
     * Retrieving document from database
     *
     * @return Document Link
     */
    public MutableLiveData<String> getDocLink(String documentReference) {
        if (documentReferencesMLD == null) documentReferencesMLD = new HashMap<>();
        if (!documentReferencesMLD.containsKey(documentReference)) {
            documentReferencesMLD.put(documentReference, new MutableLiveData<>());
            Log.d(TAG, "getDocLink: ");
            final String url = URL + "/documents/download/";
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
            requestQueue.add(request);
        }
        return documentReferencesMLD.get(documentReference);
    }

    /**
     * Deletes Document
     *
     * @param documentId
     * @param docReference
     */
    public MutableLiveData<Object> deleteDoc(String documentId, String docReference) {
        MutableLiveData<Object> mutableLiveData = new MutableLiveData<>();
        final String url = URL + "/documents/delete/" + "?documentId=" + documentId
                + "&documentReference=" + docReference;
        StringRequest request = new StringRequest(Request.Method.DELETE, url, response -> {
            Log.d(TAG, response);
            getDocs();
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
        requestQueue.add(request);
        return mutableLiveData;
    }
}
