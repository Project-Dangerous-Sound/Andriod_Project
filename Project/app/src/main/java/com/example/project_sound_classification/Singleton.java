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
    ModelAndBackEnd modelAndBackEnd;
    DataBase dataBase;
    private Soundlist soundlist[] = new Soundlist[6];
    private int color[] = {Color.DKGRAY, Color.rgb(0,0,0), Color.CYAN, Color.MAGENTA, Color.RED, Color.YELLOW};
    private String name[] = {"차경적", "개짓는소리", "사이렌", "화재경보", "도난경보", "비상경보"};
    public Singleton(){
        dataBase = new DataBase();
        modelAndBackEnd = new ModelAndBackEnd();
        createList();
        BaseSoundPriority();
        itemTouchHelperCallback = new ItemTouchHelperCallback(adapter, adapter);
    }
    public void setItemTouchHelperCallback(ListAdapter adapter){
        itemTouchHelperCallback = new ItemTouchHelperCallback(adapter, adapter);
    }
    public void createList(){
        map.put("차경적", R.drawable.image1);
        map.put("개짓는소리", R.drawable.image2);
        map.put("사이렌", R.drawable.image4);
        map.put("화재경보", R.drawable.image5);
        map.put("도난경보", R.drawable.image6);
        map.put("비상경보", R.drawable.image7);
    }
    private void BaseSoundPriority(){
        for(int i = 0;i<6;i++){
            soundlist[i] = new Soundlist(color[i], name[i], i + 1);
        }
    }
    public Soundlist[] getSoundlist(){
        return soundlist;
    }

    public void setSoundlist(Soundlist[] soundlist){
        this.soundlist = soundlist;
    }

}
