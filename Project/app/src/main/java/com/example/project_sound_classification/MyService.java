package com.example.project_sound_classification;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.project_sound_classification.librosafeature.WavFileException;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyService extends Service {
    //private BackgroundTask
    private float standfloat = 0.4f;

    public MyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class BackgroundTask extends AsyncTask<Intent, String, Intent> {

        @RequiresApi(api= Build.VERSION_CODES.CUPCAKE)
        @Override
        protected Intent doInBackground(Intent... intents) {

            while (!isCancelled()){
                try{
                    HomeScreen.singleton.modelAndBackEnd.startRecoding(getExternalFilesDir(null).getAbsolutePath());
                    Thread.sleep(1000);
                    String s = HomeScreen.singleton.modelAndBackEnd.stopRecoding();
                    if (s != null){

                    }
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (WavFileException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        }
    }
    /*private class ActionThread extends Thread{
        String audiopath;
        boolean isaction_check = false;
        public ActionThread(){

        }
        public void run(){
            File audioFile = new File(audiopath);
            ServerRequst(audioFile);
            is_running = true;
        }
        public void setAudiopath(String audiopath){
            this.audiopath = audiopath;
        }
        public void setIsaction_check(boolean isaction_check){
            this.isaction_check = isaction_check;
        }
    }*/
    /*private void ServerRequst(File audioFile){
        RequestBody requestBody = RequestBody.create(MediaType.parse("audio/wav"), audioFile);
        MultipartBody.Part audioPart = MultipartBody.Part.createFormData("audio", audioFile.getName(), requestBody);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(90, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://15.164.76.29:5000")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        MyApi myApi = retrofit.create(MyApi.class);
        Call<ApiResponse> call = myApi.uploadAudio(audioPart);
        call.enqueue(new Callback<ApiResponse>() {

            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                ApiResponse result = response.body();
                Log.v("서버로 부터 받음", "서버로 받음");
                audioFile.delete();
                String softemax_string = result.getMessage();
                String softmax_arr[] = softemax_string.split(" ");
                float softmax[] = new float[6];
                for (int i = 0;i<6;i++){
                    softmax[i] = Float.parseFloat(softmax_arr[i]);
                }
                try {
                    Weight_calc(softmax);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                is_running = false;
            }
        });
    }*/
    /*private void Weight_calc(float[] softmax) throws JSONException {
        List<MainActivity.Mapping> list = new ArrayList<>();
        for (int i = 0;i<softmax.length;i++)
            if (softmax[i] >= standfloat) {
                list.add(new MainActivity.Mapping(softmax[i], i));
            }
        Soundlist soundlist[] = HomeScreen.singleton.dataBase.currentSoundDatabase();
        Arrays.sort(soundlist);
        for(int i = 0;i<6;i++){
            for (MainActivity.Mapping mapping: list){
                if (map.get(mapping.index).equals(soundlist[i].name)){
                    mapping.value += priority_weight[i];
                }
            }
        }
        Collections.sort(list);

        if (list.size() > 1) Action(list.get(0).index, list.get(1).index);
        else if (list.size() == 1)Action(list.get(0).index, -1);
    }*/
}