package com.example.project_sound_classification;

import android.graphics.Color;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Singleton {
    ItemTouchHelperCallback itemTouchHelperCallback;
    ListAdapter adapter;
    Map<String, Integer> map = new HashMap<>();

    DataBase dataBase;
    public Singleton(){
        dataBase = new DataBase();
        createList();
        itemTouchHelperCallback = new ItemTouchHelperCallback(adapter, adapter);
    }
    public void setItemTouchHelperCallback(ListAdapter adapter){
        itemTouchHelperCallback = new ItemTouchHelperCallback(adapter, adapter);
    }
    public void createList(){
        map.put("차경적", R.drawable.image1);
        map.put("개짓는소리", R.drawable.image2);
        map.put("고양이 울음소리", R.drawable.image3);
        map.put("사이렌", R.drawable.image4);
        map.put("화재경보", R.drawable.image5);
        map.put("도난경보", R.drawable.image6);
        map.put("비상경보", R.drawable.image7);
    }


}
