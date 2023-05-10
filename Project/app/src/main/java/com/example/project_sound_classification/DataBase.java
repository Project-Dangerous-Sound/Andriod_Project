package com.example.project_sound_classification;

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
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
    private DatabaseReference  mDatabase;
    private Soundlist basePriority[] = new Soundlist[6];
    private String s[] = {"차경적", "개짓는소리", "사이렌", "화재경보", "도난경보", "비상경보"};
    public void BaseSetting(){
        for(int i = 0;i<6;i++){
            basePriority[i] = new Soundlist(HomeScreen.singleton.map.get(s[i]), s[i],i + 1);
        }
        for(int i =0;i<6;i++){
            WriteNewPriority(basePriority[i], i + 1);
        }
    }
    public void WriteNewPriority(Soundlist sound, int index){
        mDatabase.child("SoundList").child(Integer.toString(index)).setValue(sound)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.v("데이터 데이스", "데이터를 저장했습니다");
                    }
                });
    }

    public Soundlist ReadPriority(int index){
        final Soundlist[] sounds = {null};
        mDatabase.child("SoundList").child(Integer.toString(index)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue(Soundlist.class) != null){
                        sounds[0] = snapshot.getValue(Soundlist.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("FireBaseData", "loadPost:onCancelled", error.toException());
                }
            });
        return sounds[0];
    }
}
