package com.example.project_sound_classification;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;

import java.io.IOException;

public class SettingActivity extends AppCompatActivity {
    static RecyclerView setting_rv;
    SettingListAdapter adapter = new SettingListAdapter(this);
    Soundlist soundname[] = new Soundlist[6];
    int color[] = {Color.DKGRAY, Color.rgb(0,0,0), Color.CYAN, Color.MAGENTA, Color.RED, Color.YELLOW, Color.WHITE};
    static Json priorityjson;
    int defaultColor;


    static public void reset(){
        setting_rv.setAdapter(HomeScreen.singleton.adapter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 액션바 색깔을 바꾸는 코드
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.actionbar_background)));
        }
        getSupportActionBar().setTitle("설정"); // 제목 변경

        adapter = new SettingListAdapter (this);

        setContentView(R.layout.setting_rv);
        setting_rv = findViewById(R.id.setting_rv);
        //RecyclerView의 레이아웃 방식을 지정
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        setting_rv.setLayoutManager(manager);
        soundname = HomeScreen.singleton.getSoundlist();
        for (int i = 0;i<6;i++){
            adapter.addItem(soundname[i]);
        }
        //RecyclerView의 Adapter 세팅
        setting_rv.setAdapter(adapter);

    }
}