package com.example.project_sound_classification;

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
import java.util.Map;

public class PriorityActivity extends AppCompatActivity {
    static RecyclerView rv;
    ItemTouchHelper helper;
    ListAdapter adapter = new ListAdapter();
    String soundname[] = new String[7];
    static Json priorityjson;

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
        if (priorityjson == null) {
            try {
                priorityjson = new Json(this);
                HomeScreen.singleton.setPriorityjson(priorityjson);
                Log.v("생성", "생성");
            } catch (JSONException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        setContentView(R.layout.sound_list);
        rv = findViewById(R.id.rv);
        //RecyclerView의 레이아웃 방식을 지정
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);

        try {
            soundname = HomeScreen.singleton.priorityjson.getPriority();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0;i<7;i++){
            adapter.addItem(new Soundlist(HomeScreen.singleton.map.get(soundname[i]), soundname[i],i + 1));
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