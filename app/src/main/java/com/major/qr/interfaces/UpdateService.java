package com.major.qr.interfaces;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface UpdateService {

    @Headers({
            "Accept: */*",
    })
    @Multipart
    @PUT("documents/update/")
    Call<String> updateDoc(@Header("Authorization") String authorization,
                           @Part MultipartBody.Part filePart,
                           @Part("documentReference") String docRef);
}
