package com.example.project_sound_classification;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;

public class PriorityActivity extends AppCompatActivity {
    static RecyclerView rv;
    ItemTouchHelper helper;
    ListAdapter adapter = new ListAdapter();
    Soundlist soundname[] = new Soundlist[6];
    int color[] = {Color.DKGRAY, Color.rgb(0,0,0), Color.CYAN, Color.MAGENTA, Color.RED, Color.YELLOW, Color.WHITE};

    static public void reset(){
        rv.setAdapter(HomeScreen.singleton.adapter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 액션바 색깔을 바꾸는 코드
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.actionbar_background)));
        }

        getSupportActionBar().setTitle("우선순위 설정"); // 제목 변경

        adapter = new ListAdapter();

        setContentView(R.layout.sound_list);
        rv = findViewById(R.id.rv);
        //RecyclerView의 레이아웃 방식을 지정
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        soundname = HomeScreen.singleton.getSoundlist();
        Arrays.sort(soundname);
        for (int i = 0;i<6;i++){
            adapter.addItem(soundname[i]);
        }
        HomeScreen.singleton.setItemTouchHelperCallback(adapter);

        //RecyclerView의 Adapter 세팅
        rv.setAdapter(adapter);

        //ItemTouchHelper 생성
        helper = new ItemTouchHelper(HomeScreen.singleton.itemTouchHelperCallback);
        //RecyclerView에 ItemTouchHelper 붙이기
        helper.attachToRecyclerView(rv);


        //Adapter에 데이터 추가
        //SingleTon.jave에서 실행
    }
}