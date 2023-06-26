package com.example.project_sound_classification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.project_sound_classification.librosafeature.WavFileException;

import org.json.JSONException;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private BackgroundTask task;
    private float standfloat = 0.4f;
    private static int count;

    private boolean out_thread = false;
    private boolean is_running;
    private float priority_weight[] = {1.0f, 0.8f, 0.6f, 0.4f, 0.2f, 0f};

    private Map<Integer, String> map;

    private AudioRecoding audioRecoding;
    private  DataPreprocessing dataPreprocessing;
    private ActionThread actionThread;

    static class Mapping implements Comparable<MainActivity.Mapping>{
        float value;
        int index;

        public Mapping(float value, int index){
            this.value = value;
            this.index = index;
        }

        @Override
        public int compareTo(MainActivity.Mapping o){
            return Float.compare(o.value, this.value);
        }
    }
    private class ActionThread extends Thread{
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
    }
    private void mapping(){
        map.put(0, "차경적");
        map.put(1,"개짓는소리");
        map.put(2, "사이렌");
        map.put(3,"화재경보");
        map.put(4, "도난경보");
        map.put(5, "비상경보");
    }
    private boolean data_preprocessing_and_pridiction(String wav_path) throws IOException, WavFileException {
        Log.v("확인", wav_path);
        double spectrum[] = dataPreprocessing.spectrumprocesing(wav_path);
        float meanMFCCValues[][] = dataPreprocessing.mfccprocesing(spectrum);
        boolean isCheck = loadModdelANDprediction(meanMFCCValues);
        return isCheck;
    }
    private void Weight_calc(float[] softmax) throws JSONException {
        List<MainActivity.Mapping> list = new ArrayList<>();
        for (int i = 0;i<softmax.length;i++)
            if (softmax[i] >= standfloat) {
                list.add(new MainActivity.Mapping(softmax[i], i));
            }
        Soundlist soundlist[] = HomeScreen.singleton.getSoundlist();
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
    }
    private void Action(int index1, int index2){

        NotificationManager manager;
        NotificationCompat.Builder builder;

        String CHANNEL_ID = "channel1";
        String CHANEL_NAME = "Channel1";
        builder = null;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //버전 오레오 이상일 경우
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            );

            builder = new NotificationCompat.Builder(this,CHANNEL_ID);

            //하위 버전일 경우
        }else{
            builder = new NotificationCompat.Builder(this);
        }
        String s = "";
        if(index2 != -1) s = map.get(index1) + "소리와 " + map.get(index2) +"소리가 감지되었습니다.";
        else s = map.get(index1) + "소리가 감지되었습니다.";
        //알림창 제목
        builder.setContentTitle("소리 감지");

        //알림창 메시지
        builder.setContentText(s);

        builder.setSmallIcon(R.mipmap.ic_launcher);
        Notification notification = builder.build();

        //알림창 실행
        manager.notify(1,notification);

        Vibrator vibrator1 = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator1.vibrate(VibrationEffect.createOneShot(1000, 50));
        is_running = false;
    }
    private boolean loadModdelANDprediction(float [][] meanMFCC) throws IOException {
        MappedByteBuffer tflitemodel = FileUtil.loadMappedFile(this, "converted_model_4layer.tflite");
        Interpreter tflite;

        Interpreter.Options tfliteOption = new Interpreter.Options();
        tfliteOption.setNumThreads(1);
        tflite = new Interpreter(tflitemodel, tfliteOption);

        int imageTensorIndex = 0;
        int [] imgeShape = tflite.getInputTensor(imageTensorIndex).shape();
        DataType imgeDataType = tflite.getInputTensor(imageTensorIndex).dataType();
        int probabilityTensorIndex = 0;
        int probabilityShape[] = tflite.getOutputTensor(probabilityTensorIndex).shape();
        DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

        ByteBuffer inputBuffer1 = ByteBuffer.allocateDirect(38400).order(ByteOrder.nativeOrder());
        // 1 * 120 * 80 * 1
        for (int j = 0; j < 120; j++) {
            for (int k = 20; k < 100; k++) {
                inputBuffer1.putFloat(meanMFCC[j][k]);
            }
        }
        TensorBuffer outputTensorBuffer = TensorBuffer.createFixedSize(probabilityShape,probabilityDataType);
        tflite.run(inputBuffer1, outputTensorBuffer.getBuffer());
        float[] result = outputTensorBuffer.getFloatArray();
        float nonsound =  result[0];
        float checksound = result[1];
        String non = String.format("%.2f", nonsound);
        String check = String.format("%.2f", checksound);
        String s = non + " " + check;
        //Log.v("확인", Float.toString(sum) + " " + Float.toString(nonsound) + " " + Float.toString(checksound));
        return checksound - nonsound >= 0.2f;
    }
    private void startRecoding(){
        audioRecoding = new AudioRecoding();
        String s = "recoding" + Integer.toString(count);
        audioRecoding.startRecording(getExternalFilesDir(null).getAbsolutePath(), s, this);
        count++;
    }

    private void stopRecoding() throws JSONException, IOException, WavFileException {
        audioRecoding.stopRecode();
        String audiopath = audioRecoding.getOutputpath();
        uploadAudioFile(audiopath);
    }
    private void uploadAudioFile(String audioFilePath) throws JSONException, IOException, WavFileException {
        boolean create;
        try {
            create = data_preprocessing_and_pridiction(audioFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (WavFileException e) {
            throw new RuntimeException(e);
        }
        if(create) {
            actionThread.setAudiopath(audioFilePath);
            if (!is_running)  actionThread.run();
        }
        else{
            File audioFile = new File(audioFilePath);
            audioFile.delete();
        }
    }
    private void ServerRequst(File audioFile){
        RequestBody requestBody = RequestBody.create(MediaType.parse("audio/wav"), audioFile);
        MultipartBody.Part audioPart = MultipartBody.Part.createFormData("audio", audioFile.getName(), requestBody);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(90, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://52.78.138.181:8080")
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
    }


    public MyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        task =new BackgroundTask();
        task.execute();
        dataPreprocessing = new DataPreprocessing();
        actionThread = new ActionThread();
        map = new HashMap<>();
        mapping();
        initializeNotification();
        return START_NOT_STICKY;
    }
    public void initializeNotification()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText("녹음 중..");
        style.setBigContentTitle(null);
        style.setSummaryText("백그라운드 실행중..");
        builder.setContentText(null);
        builder.setContentTitle(null);
        builder.setOngoing(true);
        builder.setStyle(style);
        builder.setWhen(0);
        builder.setShowWhen(false);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(new NotificationChannel("1", "포그라운드 서비스", NotificationManager.IMPORTANCE_NONE));
        }
        Notification notification = builder.build();
        startForeground(1, notification);
    }
    class BackgroundTask extends AsyncTask< Intent, String, Intent> {

        @RequiresApi(api= Build.VERSION_CODES.CUPCAKE)
        @Override
        protected Intent doInBackground(Intent... intents) {

            while (!isCancelled()){
                try {
                    startRecoding();
                    Thread.sleep(2000);
                    stopRecoding();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
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
        //상태확인
        @Override
        protected void onProgressUpdate(String ... String){

        }

        @Override
        protected void onCancelled(){

        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        task.cancel(true);
    }

}