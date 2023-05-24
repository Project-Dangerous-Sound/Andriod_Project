package com.example.project_sound_classification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class ForGraoundService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) { //핸들링 하는 부분
        MainActivity.Threads threads1;
        stopSelf(); //서비스 종료
    }
}
