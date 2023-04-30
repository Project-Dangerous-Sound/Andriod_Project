package com.example.project_sound_classification;
import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.project_sound_classification.librosafeature.ExtAudioRecorder;

import java.io.*;
public class AudioRecoding {
    private MediaRecorder recorder;
    private ExtAudioRecorder recorder1;
    private String outputpath;
    private String test;
    private String breksound;
    public void startRecording(String outputDir, String outputName, Context context) {
        /*recorder = new MediaRecorder(context);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        */
        recorder1 = ExtAudioRecorder.getInstanse(false);
        outputpath = outputDir + "/" + outputName + ".wav";
        test = outputDir + "/" + "0b80f560" + ".wav";
        breksound = outputDir + "/" + "020eb9f6" + ".wav";
        //recorder.setOutputFile(outputpath);
        recorder1.setOutputFile(outputpath);

        /*try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        recorder1.prepare();
        recorder1.start();
    }

    public void stopRecode(){
        /*if (recorder != null){
            recorder.stop();
            recorder.release();
            recorder = null;
        }*/
        if (recorder1 != null) {
            recorder1.stop();
            recorder1.release();
            recorder1 = null;
        }
    }

    public String getOutputpath(){
        return outputpath;
    }
    public String getTest(){
        return test;
    }

    public String getBrekSound(){ return breksound; }
}
