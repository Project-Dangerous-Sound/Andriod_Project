package com.example.project_sound_classification;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.annotation.RequiresPermission;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.project_sound_classification.audiofeature.MFCC;
import com.example.project_sound_classification.databinding.ActivityMainBinding;
import com.example.project_sound_classification.librosafeature.WavFile;
import com.example.project_sound_classification.librosafeature.WavFileException;

import okhttp3.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.*;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.w3c.dom.Node;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.util.*;
import java.text.DecimalFormat;
import java.nio.MappedByteBuffer;
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
    private Vibrator vibrator;
    private TextView textView;
    private MFCC mfcc;
    private LinearLayout bacground;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private AudioRecoding audioRecoding;
    private Map<Integer, String> map;
    private  DataPreprocessing dataPreprocessing;
    private float standfloat = 0.4f;
    private float priority_weight[] = {1.0f, 0.8f, 0.6f, 0.4f, 0.2f, 0f};
    private int color[] = {Color.DKGRAY, Color.TRANSPARENT, Color.CYAN, Color.MAGENTA, Color.RED, Color.YELLOW};

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
    private float[] creatre_MFCC(String wav_path) throws IOException, WavFileException {

        double spectrum[] = dataPreprocessing.spectrumprocesing(wav_path);
        float meanMFCCValues[] = dataPreprocessing.mfccprocesing(spectrum);

        return meanMFCCValues;
    }
    private void Tensor(float [] meanMFCC){

    }
    private void Weight_calc(float[] softmax) throws JSONException {
        List<Mapping> list = new ArrayList<>();
        for (int i = 0;i<softmax.length;i++)
            if (softmax[i] >= standfloat) {
                list.add(new Mapping(softmax[i], i));
                Log.v("통과", Integer.toString(i) + " " + Float.toString(softmax[i]) + map.get(i));
            }
        String priorityName[] = HomeScreen.singleton.priorityjson.getPriority();

        for(int i = 0;i<6;i++){
            for (Mapping mapping: list){
                if (map.get(mapping.index).equals(priorityName[i])){
                    mapping.value += priority_weight[i];
                }
            }
        }
        Collections.sort(list);
        Action(list.get(0).index);
    }

    private void Action(int index){
        bacground.setBackgroundColor(color[index]);
        Log.v("위험한 소리: ", map.get(index));
        Vibrator vibrator1 = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator1.vibrate(VibrationEffect.createOneShot(1000, 50));
    }

    private void startRecoding(){
        Log.v("녹음", "녹음 시작");
        audioRecoding = new AudioRecoding();
        audioRecoding.startRecording(getExternalFilesDir(null).getAbsolutePath(), "recoding", this);
        Log.v("녹음", "녹음 시작");
    }

    private void stopRecoding() throws JSONException, IOException, WavFileException {
        audioRecoding.stopRecode();
        String audiopath = audioRecoding.getOutputpath();
        Log.v("녹음", "녹음 중지");
        uploadAudioFile(audiopath);
    }
    private void playAudio(File file) {
        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void uploadAudioFile(String audioFilePath) throws JSONException, IOException, WavFileException {
        Log.v("BackEnd", "서버로 .wav파일 보냄");
        File audioFile = new File(audioFilePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("audio/wav"), audioFile);
        MultipartBody.Part audioPart = MultipartBody.Part.createFormData("audio", audioFile.getName(), requestBody);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://10.0.2.2:5000/upload_audio/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApi myApi = retrofit.create(MyApi.class);
        Call<ApiResponse> call = myApi.uploadAudio(audioPart);
        call.enqueue(new Callback<ApiResponse>() {

            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                ApiResponse result = response.body();

                Log.v("확인", "서버에서 받음");
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.v("확인", t.getMessage());
            }
        });
        float softmax[] = new float[6];
        for (int i = 0;i<6;i++) softmax[i] = (float)Math.random();
        Weight_calc(softmax);
        float [] create = creatre_MFCC(audioFilePath);
        for(float arr: create){
            Log.v("MFCC", Float.toString(arr));
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        try {
            HomeScreen.singleton.setPriorityjson(new Json(this));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dataPreprocessing = new DataPreprocessing();

        textView = findViewById(R.id.textview_first);
        bacground = findViewById(R.id.background);
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

        Button recodeingstartbutton = (Button) findViewById(R.id.button);
        Button recodingstopbutton = (Button) findViewById(R.id.button2);
        recodeingstartbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecoding();
            }
        });
        recodingstopbutton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    stopRecoding();
                } catch (JSONException | IOException | WavFileException e) {
                    throw new RuntimeException(e);
                }
            }
        }));
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 툴바 글자 안보이게 만들어주는 코드
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
    // 스위치 버튼이 선택된 경우(즉, 상태가 "켜짐"인 경우)에만 짧은 진동(100밀리초)을 트리거합니다.
    // 진동을 트리거해야 하는 이벤트가 발생할 때마다 vibrate() 메서드를 호출합니다.
    // 예를 들어, 버튼을 클릭할 때 진동하도록 하려면 다음 코드를 OnClickListener에 추가할 수 있습니다.
    private void vibrate() {
        Button button = findViewById(R.id.app_bar_switch);
        Button recodeingstartbutton = findViewById(R.id.button);
        Button recodingstopbutton = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrate();
                // add more code here to handle the button click
            }
        });


        if (vibrator == null) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        }
        if (vibrator.hasVibrator() && ((Switch)findViewById(R.id.app_bar_switch)).isChecked()) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }
    private void Recoding(){

    }



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