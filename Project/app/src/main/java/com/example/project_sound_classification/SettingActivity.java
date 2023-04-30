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

import yuku.ambilwarna.AmbilWarnaDialog;

public class SettingActivity extends AppCompatActivity {
    static RecyclerView setting_rv;
    ItemTouchHelper helper;
    SettingListAdapter adapter = new SettingListAdapter();
    String soundname[] = new String[7];
    int color[] = new int[7];
    static Json priorityjson;

    Button setting_btn;
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

        adapter = new SettingListAdapter ();
        if (priorityjson == null) {
            try {
                priorityjson = new Json(this);
                HomeScreen.singleton.setPriorityjson(priorityjson);
                Log.v("생성", "생성");
            } catch (JSONException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        setContentView(R.layout.setting_rv);
        setting_rv = findViewById(R.id.setting_rv);
        //RecyclerView의 레이아웃 방식을 지정
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        setting_rv.setLayoutManager(manager);

        try {
            soundname = HomeScreen.singleton.priorityjson.getPriority();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0;i<7;i++){
            adapter.addItem(new Soundlist(HomeScreen.singleton.map.get(soundname[i]), soundname[i],i + 1/*, color[i]*/));
        }
        //RecyclerView의 Adapter 세팅
        setting_rv.setAdapter(adapter);


    }
    public void openColorPicker(){
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                //취소
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {

                //선택 한 색을 변수에 담는다.
                defaultColor = color;
                Log.v("색깔코드 ", Integer.toHexString(defaultColor));
                //색을 담은 변수를 배경색으로 설정한다.
                adapter.setting_btn.setBackgroundColor(defaultColor);
            }
        });
        colorPicker.show();
    }
}