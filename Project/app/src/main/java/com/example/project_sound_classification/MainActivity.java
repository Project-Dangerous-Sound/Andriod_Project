package com.example.project_sound_classification;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.project_sound_classification.databinding.ActivityMainBinding;

import org.jetbrains.annotations.Nullable;

public class MainActivity extends AppCompatActivity {
    private Vibrator vibrator;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;


    //-----------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        getSupportActionBar().setDisplayShowTitleEnabled(false); // 툴바 글자 안보이게
    }

    // 이 코드는 Vibrator 개체(아직 존재하지 않는 경우)를 만들고,
    // 스위치 버튼이 선택된 경우(즉, 상태가 "켜짐"인 경우)에만 짧은 진동(100밀리초)을 트리거합니다.
    // 진동을 트리거해야 하는 이벤트가 발생할 때마다 vibrate() 메서드를 호출합니다.
    // 예를 들어, 버튼을 클릭할 때 진동하도록 하려면 다음 코드를 OnClickListener에 추가할 수 있습니다.
    private void vibrate() {
        Button button = findViewById(R.id.app_bar_switch);
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