package com.major.qr.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.major.qr.models.Doc
import com.major.qr.ui.LoginActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * @author shvmsaini
 */
class DocumentViewModel : ViewModel() {
    private val TAG = DocumentViewModel::class.java.simpleName
    private var docList: MutableLiveData<ArrayList<Doc>>? = null
    private var documentReferencesMLD: HashMap<String, MutableLiveData<String>>? = null

    /**
     * Used to upload doc to database
     *
     * @param file File to upload
     */
    fun uploadDoc(file: File): MutableLiveData<String> {
        val mutableLiveData = MutableLiveData<String>()
        val extension = file.name.substring(file.name.lastIndexOf('.') + 1)
        val type: String
        type = if (extension == "pdf") "pdf" else "image"
        val URL = LoginActivity.URL + UPLOAD_MAPPING
        AndroidNetworking.upload(URL)
            .addHeaders("Authorization", LoginActivity.ACCESS_TOKEN)
            .addMultipartFile("document", file)
            .addMultipartParameter("documentType", type)
            .setTag("uploadingFile")
            .setPriority(Priority.HIGH)
            .build()
            .setUploadProgressListener { bytesUploaded: Long, totalBytes: Long ->
                Log.d(
                    TAG,
                    "Bytes Uploaded$bytesUploaded"
                )
            }
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    docs
                    mutableLiveData.postValue(response.toString())
                    Log.d(TAG, "onResponse: $response")
                }

                override fun onError(anError: ANError) {
                    Log.d(TAG, "onError: $anError")
                }
            })
        return mutableLiveData
    }

    /**
     * Used to update the doc in database
     *
     * @param docReference reference id of doc
     * @param file         file to replace it with
     */
    fun updateDoc(docReference: String?, file: File?): MutableLiveData<String> {
        val updateDocResponse = MutableLiveData<String>()
        val URL = LoginActivity.URL + "/documents/update/"
        AndroidNetworking.put(URL)
            .addHeaders("Authorization", LoginActivity.ACCESS_TOKEN)
            .addFileBody(file)
            .addBodyParameter("documentReference", docReference)
            .setTag("updatingFile")
            .setPriority(Priority.IMMEDIATE)
            .build() //                .setUploadProgressListener((bytesUploaded, totalBytes) -> Log.d(TAG, "" + bytesUploaded))
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    updateDocResponse.postValue(response.toString())
                    Log.d(TAG, "onResponse: $response")
                }

                override fun onError(anError: ANError) {
                    Log.d(TAG, "onError: $anError")
                }
            })
        return updateDocResponse
    }

    val docs: MutableLiveData<ArrayList<Doc>>
        /**
         * Retrievs all documents from database
         *
         * @return List of all docs uploaded by user
         */
        get() {
            if (docList == null) docList = MutableLiveData()
            val url = LoginActivity.URL + "/documents/getDocuments"
            val request: StringRequest =
                object : StringRequest(Method.GET, url, Response.Listener { response: String? ->
                    val docs = ArrayList<Doc>()
                    try {
                        val jsonArray = JSONArray(response)
                        Log.d(TAG, "jsonArray = $jsonArray")
                        for (i in 0 until jsonArray.length()) {
                            val `object` = jsonArray.getJSONObject(i)
                            val d = Doc(
                                `object`.getString("documentType"),
                                `object`.getString("documentReference"),
                                `object`.getString("documentId")
                            )
                            docs.add(d)
                        }
                        docList!!.postValue(docs)
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                }, Response.ErrorListener { error: VolleyError -> Log.d(TAG, error.toString()) }) {
                    override fun getHeaders(): HashMap<String?, String?> {
                        return object : HashMap<String?, String?>() {
                            init {
                                put("Authorization", LoginActivity.ACCESS_TOKEN)
                            }
                        }
                    }
                }
            LoginActivity.requestQueue?.add(request)
            return docList!!
        }

    /**
     * Retrieving document from database
     *
     * @return Document Link
     */
    fun getDocLink(documentReference: String): MutableLiveData<String> {
        if (documentReferencesMLD == null) documentReferencesMLD = HashMap()
        if (!documentReferencesMLD!!.containsKey(documentReference)) {
            documentReferencesMLD!![documentReference] = MutableLiveData()
            Log.d(TAG, "getDocLink: ")
            val url = LoginActivity.URL + "/documents/download/"
            val request: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response: String ->
                    Log.d(TAG, response)
                    documentReferencesMLD!![documentReference]!!.postValue(response)
                }, Response.ErrorListener { error: VolleyError -> Log.d(TAG, error.toString()) }) {
                    override fun getHeaders(): HashMap<String?, String?> {
                        return object : HashMap<String?, String?>() {
                            init {
                                put("Authorization", LoginActivity.ACCESS_TOKEN)
                                put("Content-Type", "application/json")
                            }
                        }
                    }

                    override fun getBody(): ByteArray {
                        return documentReference.toByteArray(StandardCharsets.UTF_8)
                    }
                }
            LoginActivity.requestQueue?.add(request)
        }
        return documentReferencesMLD!![documentReference]!!
    }

    /**
     * Deletes Document
     *
     * @param documentId
     * @param docReference
     */
    fun deleteDoc(documentId: String, docReference: String): MutableLiveData<Any> {
        val mutableLiveData = MutableLiveData<Any>()
        val url = (LoginActivity.URL + "/documents/delete/" + "?documentId=" + documentId
                + "&documentReference=" + docReference)
        val request: StringRequest =
            object : StringRequest(Method.DELETE, url, Response.Listener { response: String ->
                Log.d(TAG, response)
                docs
                mutableLiveData.postValue(response)
            }, Response.ErrorListener { error: VolleyError -> Log.d(TAG, error.toString()) }) {
                override fun getHeaders(): HashMap<String?, String?> {
                    return object : HashMap<String?, String?>() {
                        init {
                            put("Authorization", LoginActivity.ACCESS_TOKEN)
                            put("accept", "*/*")
                        }
                    }
                }
            }
        LoginActivity.requestQueue?.add(request)
        return mutableLiveData
    }

    companion object {
        private const val UPLOAD_MAPPING = "/documents/upload"
    }
}