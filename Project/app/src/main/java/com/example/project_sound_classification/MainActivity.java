package com.example.project_sound_classification;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.example.project_sound_classification.audiofeature.MFCC;
import com.example.project_sound_classification.databinding.ActivityMainBinding;
import com.example.project_sound_classification.databinding.HomeScreenOneBinding;
import com.example.project_sound_classification.librosafeature.WavFileException;

import okhttp3.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.*;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

import org.json.JSONException;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.nio.MappedByteBuffer;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    static class Mapping implements Comparable<Mapping>{
        float value;
        int index;

        public Mapping(float value, int index){
            this.value = value;
            this.index = index;
        }

        @Override
        public int compareTo(Mapping o){
            return Float.compare(o.value, this.value);
        }
    }

    public class Threads extends Thread{
        public Threads(){

        }
        public void run(){

            while (!out_thread){
                try{
                    startRecoding();
                    Threads.sleep(1800);
                    stopRecoding();
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (WavFileException e) {
                    throw new RuntimeException(e);
                }
            }
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
    private static int count;
    private static boolean forground = false;
    private Vibrator vibrator;
    private TextView sound1, sound2, soundone;
    private ImageView background1, background2, background_one;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private AudioRecoding audioRecoding;
    private Map<Integer, String> map;
    private  DataPreprocessing dataPreprocessing;
    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";
    private float standfloat = 0.4f;
    private float priority_weight[] = {1.0f, 0.8f, 0.6f, 0.4f, 0.2f, 0f};
    private int color[] = {Color.DKGRAY, Color.rgb(0,0,0), Color.CYAN, Color.MAGENTA, Color.RED, Color.YELLOW};
    private int imageSrc [] = {R.drawable.image1, R.drawable.image2,
            R.drawable.image4, R.drawable.image5, R.drawable.image6,
            R.drawable.image7, R.drawable.wave}; //이미지를 변경하기 위해서 이미지 소스를 배열로 저장

    private Threads threads;
    private ActionThread actionThread;
    private boolean is_running;
    private boolean out_thread = false;
    private boolean already = false;

    private int mNumFrames;
    private int mSampleRate;
    private int mChannels;
    private void mapping(){
        map.put(0, "차경적");
        map.put(1,"개짓는소리");
        map.put(2, "사이렌");
        map.put(3,"화재경보");
        map.put(4, "도난경보");
        map.put(5, "비상경보");
    }
    private boolean data_preprocessing_and_pridiction(String wav_path) throws IOException, WavFileException {
        double spectrum[] = dataPreprocessing.spectrumprocesing(wav_path);
        float meanMFCCValues[][] = dataPreprocessing.mfccprocesing(spectrum);
        boolean isCheck = loadModdelANDprediction(meanMFCCValues);
        return isCheck;
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

        ByteBuffer inputBuffer1 = ByteBuffer.allocateDirect(57600).order(ByteOrder.nativeOrder());
        // 1 * 120 * 80 * 1
        for (int j = 0; j < 120; j++) {
            for (int k = 0; k < 120; k++) {
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
    private void Weight_calc(float[] softmax) throws JSONException {
        List<Mapping> list = new ArrayList<>();
        for (int i = 0;i<softmax.length;i++)
            if (softmax[i] >= standfloat) {
                list.add(new Mapping(softmax[i], i));
            }
        Soundlist soundlist[] = HomeScreen.singleton.getSoundlist();
        Arrays.sort(soundlist);
        for(int i = 0;i<6;i++){
            for (Mapping mapping: list){
                if (map.get(mapping.index).equals(soundlist[i].name)){
                    mapping.value += priority_weight[i];
                }
            }
        }
        Collections.sort(list);

        if (list.size() > 1) Action(list.get(0).index, list.get(1).index);
        else if (list.size() == 1)Action(list.get(0).index, -1);
    }

    private void Action(int index, int index2){
        if (index2 != -1) {
            setContentView(R.layout.home_screen);
            background1 = findViewById(R.id.background1);
            background2 = findViewById(R.id.background2);

            sound1 = findViewById(R.id.sound1);
            sound2 = findViewById(R.id.sound2);
            ImageButton imgBtn = (ImageButton) findViewById(R.id.start_btn_two);

            //--------------------------------------//
            background1.setBackgroundColor(color[index]);
            background2.setBackgroundColor(color[index2]);

            sound1.setText(map.get(index));
            sound2.setText(map.get(index2));

            imgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!already){
                        Log.v("한번 클릭", "시작");
                        //$$$$$$$$$$$$$$$$$$시작이벤트 작성$$$$$$$$$$$$$$$$$$$$$$
                        Start_Message();
                    }
                }
            });

            imgBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (already){  //작동중일때만 종료 가능
                        Log.v("길게누르면", "종료");

                        soundone.setText("화면을 한번 눌러주세요!");
                        already = !already;

                        //여기에 종료 이벤트 작성
                        End_Message();
                    }
                    return true;
                }
            });
        }
        else {
            setContentView(R.layout.home_screen_one);
            background_one = findViewById(R.id.background_one);
            soundone = findViewById(R.id.soundone);
            ImageButton imgBtn = (ImageButton) findViewById(R.id.start_btn);

            if (background_one != null) {
                background_one.setBackgroundColor(color[index]);
                background_one.setImageResource(imageSrc[index]);
            } else {
                Log.v("MyActivity", "ImageView is null");
            }
            if (soundone != null){
                soundone.setText(map.get(index));
            } else {
                Log.v("MyActivity", "TextView is null");
            }

            imgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!already){
                        Log.v("한번 클릭", "시작");
                        //$$$$$$$$$$$$$$$$$$시작이벤트 작성$$$$$$$$$$$$$$$$$$$$$$
                        Start_Message();
                    }
                }
            });

            imgBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (already){  //작동중일때만 종료 가능
                        Log.v("길게누르면", "종료");

                        soundone.setText("화면을 한번 눌러주세요!");
                        already = !already;

                        //여기에 종료 이벤트 작성
                        End_Message();
                    }
                    return true;
                }
            });

        }


        Vibrator vibrator1 = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator1.vibrate(VibrationEffect.createOneShot(1000, 50));
        is_running = false;
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

    //-----------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dataPreprocessing = new DataPreprocessing();


        forground = false;

        background_one = findViewById(R.id.background_one);

        soundone = findViewById(R.id.soundone);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        if (map == null){
            map = new HashMap<>();
            mapping();
        }
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
        int permission1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissing3 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);
        // 권한이 열려있는지 확인
        if (permission == PackageManager.PERMISSION_DENIED || permission1 == PackageManager.PERMISSION_DENIED
                || permission2 == PackageManager.PERMISSION_DENIED || permissing3 == PackageManager.PERMISSION_DENIED) {
            // 마쉬멜로우 이상버전부터 권한을 물어본다
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 권한 체크(READ_PHONE_STATE의 requestCode를 1000으로 세팅
                requestPermissions(
                        new String[]{Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.INTERNET},
                        1000);
            }
        }

       /* Button recodeingstartbutton = (Button) findViewById(R.id.button_one);
        Button recodeingstopbutton = (Button) findViewById(R.id.button_two);*/
        threads = new Threads();
        actionThread = new ActionThread();
        /*recodeingstartbutton.setOnClickListener(new View.OnClickListener() {  //아래 코드로 변경 요청
            @Override
            public void onClick(View view) {
                Start_Message();
            }
        });
        recodeingstopbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                End_Message();
            }
        });*/

        //코드변경
        ImageButton imgBtn = (ImageButton) findViewById(R.id.start_btn);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!already){
                    Log.v("한번 클릭", "시작");
                    //$$$$$$$$$$$$$$$$$$시작이벤트 작성$$$$$$$$$$$$$$$$$$$$$$
                    Start_Message();
                }
            }
        });

        imgBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (already){  //작동중일때만 종료 가능
                    Log.v("길게누르면", "종료");

                    soundone.setText("화면을 한번 눌러주세요!");
                    already = !already;

                    //여기에 종료 이벤트 작성
                    End_Message();
                }
                return true;
            }
        });




        // getSupportActionBar().setDisplayShowTitleEnabled(false); 툴바 글자 안보이게 만들어주는 코드

        getSupportActionBar().setTitle("위험한 소리 알리미"); // 제목 변경

        // 앱바의 색깔 바꾸는 코드
        // getWindow().setStatusBarColor(Color.DKGRAY);

        // 액션바 색깔을 바꾸는 코드
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.actionbar_background)));
        }

    }
    private void Start_Message(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("녹음을 시작하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 종료 버튼을 눌렀을 때 앱을 종료합니다.
                        soundone.setText("소리 듣는 중...");
                        already = !already; //already 현재 작동중인지  확인하기 위한 변수
                        out_thread = false;
                        threads.start();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 취소 버튼을 눌렀을 때 다이얼로그를 닫습니다.
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void End_Message(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("녹음을 종료하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        out_thread = true;
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void Ready_Message(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("현재 준비중입니다.")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 종료 버튼을 눌렀을 때 앱을 종료합니다.

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        // READ_PHONE_STATE의 권한 체크 결과를 불러온다
        super.onRequestPermissionsResult(requestCode, permissions, grandResults);
        if (requestCode == 1000) {
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            // 권한 체크에 동의를 하지 않으면 안드로이드 종료
            if (check_result == true) {

            } else {
                finish();
            }
        }
    }

    // 이 코드는 Vibrator 개체(아직 존재하지 않는 경우)를 만들고,

    //-----------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //-----------------------------------------------------------------------------------------------------------------------------
    // 각종 메뉴(알림우선순위, 로그, 설정, 이용안내)들을 선택시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml

        int id = item.getItemId();
        /*
        if (id == R.id.action_dark_mode) {
            // 액티비티의 스타일을 변경하여 배경을 어둡게 만듭니다.
            setTheme(R.style.AppTheme_Dark);
            recreate(); // 액티비티를 다시 시작합니다.
            return true;
        }

        else if (id == R.id.action_light_mode) {
            // 액티비티의 스타일을 변경하여 배경을 밝게 만듭니다.
            setTheme(R.style.AppTheme);
            recreate(); // 액티비티를 다시 시작합니다.
            return true;
        }
        */

        // 알림우선순위 버튼을 클릭 시
        if (id == R.id.Priorty) {
            Intent intent = new Intent(MainActivity.this, PriorityActivity.class);
            startActivity(intent);
            return true;
        }

        //noinspection SimplifiableIfStatement
        // 로그 버튼을 클릭 시
        else if (id == R.id.Log) {
            Intent intent = new Intent(MainActivity.this, LogActivity.class);
            startActivity(intent);
            return true;
        }

        //noinspection SimplifiableIfStatement
        // 설정 버튼을 클릭 시
        else if (id == R.id.Settings) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        }

        //noinspection SimplifiableIfStatement
        // 이용안내 버튼을 클릭 시
        else if (id == R.id.ServiceInfo) {
            Intent intent = new Intent(MainActivity.this, ServiceInfoActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.BackgroundStart){
            Intent service;
            out_thread = true;
            service = new Intent(this, MyService.class);
            startService(service);
        }
        else if (id == R.id.BackgroundEnd){
            Intent service;
            service = new Intent(this, MyService.class);
            stopService(service);
        }
        return super.onOptionsItemSelected(item);

    }


    //-----------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


    //-----------------------------------------------------------------------------------------------------------------------------
    // 정말로 종료하시겠습니까? 문구를 띄움과 동시에 어플을 종료할 지 말지 결정하는 다이얼로그
    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("정말로 종료하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 종료 버튼을 눌렀을 때 앱을 종료합니다.
                        finish();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 취소 버튼을 눌렀을 때 다이얼로그를 닫습니다.
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }


}