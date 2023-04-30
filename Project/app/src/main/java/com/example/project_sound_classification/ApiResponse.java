package com.example.project_sound_classification;
import com.google.gson.annotations.SerializedName;

import okhttp3.*;
import retrofit2.*;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
public class ApiResponse {
    @SerializedName("message")
    private float[] message;

    public float[] getMessage(){
        return message;
    }
}
