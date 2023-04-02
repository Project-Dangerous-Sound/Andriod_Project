package com.example.project_sound_classification;
import okhttp3.*;
import retrofit2.*;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MyApi {
    @Multipart
    @POST("upload_audio")
    Call<ApiResponse> uploadAudio(@Part MultipartBody.Part audioPart);
}