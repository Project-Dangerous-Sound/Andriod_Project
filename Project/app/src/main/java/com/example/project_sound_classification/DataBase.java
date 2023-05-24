package com.example.project_sound_classification;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DataBase {
    public DataBase(){
        mDatabase = FirebaseDatabase.getInstance().getReference("SoundList");
    }
    private DatabaseReference  mDatabase;
    private Soundlist basePriority[] = new Soundlist[6];
    private String s[] = {"차경적", "개짓는소리", "사이렌", "화재경보", "도난경보", "비상경보"};
    int color[] = {Color.DKGRAY, Color.rgb(0,0,0), Color.MAGENTA, Color.RED, Color.YELLOW, Color.WHITE};

    public void BaseSetting(){
        for(int i = 0;i<6;i++){
            basePriority[i] = new Soundlist( color[i], s[i],i + 1);
        }
        for(int i =0;i<6;i++){
            WriteNewPriority(basePriority[i], s[i]);
        }
    }
    public void WriteNewPriority(Soundlist sound, String name){
        mDatabase.child(name).setValue(sound);
    }
    public void SetColor(String name, int color){
        mDatabase.child("SoundList").child(name).child("color").setValue(color);
    }

    public Soundlist ReadPriority(String name){
        final Soundlist[] sounds = {null};
        mDatabase.child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue(Soundlist.class) != null){
                    sounds[0] = snapshot.getValue(Soundlist.class);
                    Log.v("FireBaseData", "getData" + sounds[0].name);
                } else {
                    Log.v("확인", "실패");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.v("FireBaseData", "loadPost:onCancelled", error.toException());
            }
        });
        return sounds[0];
    }
    public int ReadColor(String name){
        final int[] color = {0};
        mDatabase.child(name).child("color").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(Integer.class) != null) color[0] = snapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return color[0];
    }
    public Soundlist[] currentSoundDatabase(){
        Soundlist[] soundlists = new Soundlist[6];
        for(int i = 0;i<6;i++){
            soundlists[i] = ReadPriority(s[i]);
            break;
        }
        return soundlists;
    }
    public void setDatabase(String name[]){
        for(int i = 0;i<6;i++) {
            WriteNewPriority(new Soundlist(ReadColor(s[i]), name[i], i + 1), name[i]);
        }
    }
}
