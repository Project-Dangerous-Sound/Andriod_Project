package com.example.project_sound_classification;
import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.*;
public class AudioRecoding {
    private MediaRecorder recorder;
    private String outputpath;
    public void startRecording(String outputDir, String outputName, Context context) {
        recorder = new MediaRecorder(context);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        outputpath = outputDir + "/" + outputName + ".wav";
        recorder.setOutputFile(outputpath);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recorder.start();
    }

    public void stopRecode(){
        if (recorder != null){
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    public String getOutputpath(){
        return outputpath;
    }
}
