package com.example.project_sound_classification;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PriorityActivity extends AppCompatActivity {
    static RecyclerView rv;
    ListAdapter adapter;
    ItemTouchHelper helper;
    static Singleton singleton;
    static public void reset(){
        rv.setAdapter(singleton.adapter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound_list);
        singleton = new Singleton();
        rv = findViewById(R.id.rv);
        //RecyclerView의 레이아웃 방식을 지정
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);

        //RecyclerView의 Adapter 세팅
        adapter = new ListAdapter();
        rv.setAdapter(singleton.adapter);

        //ItemTouchHelper 생성
        helper = new ItemTouchHelper(singleton.itemTouchHelperCallback);
        //RecyclerView에 ItemTouchHelper 붙이기
        helper.attachToRecyclerView(rv);


        //Adapter에 데이터 추가
        //SingleTon.jave에서 실행
    }
}