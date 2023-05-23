package com.example.project_sound_classification;

import android.util.Log;
import android.widget.Toast;

import com.example.project_sound_classification.librosafeature.WavFileException;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.concurrent.TimeUnit;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import okhttp3.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.*;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;


import androidx.appcompat.app.AppCompatActivity;


public class ModelAndBackEnd extends AppCompatActivity {
    private AudioRecoding audioRecoding;
    private static int count;
    private  DataPreprocessing dataPreprocessing;
    public ModelAndBackEnd(){
        audioRecoding = new AudioRecoding();
        dataPreprocessing = new DataPreprocessing();
    }
    private void startRecoding(){
        audioRecoding = new AudioRecoding();
        String s = "recoding" + Integer.toString(count);
        audioRecoding.startRecording(getExternalFilesDir(null).getAbsolutePath(), s, this);
        count++;
    }

    private boolean stopRecoding() throws JSONException, IOException, WavFileException {
        audioRecoding.stopRecode();
        String audiopath = audioRecoding.getOutputpath();
        return uploadAudioFile(audiopath);
    }
    private boolean uploadAudioFile(String audioFilePath) throws JSONException, IOException, WavFileException {
        boolean create;
        try {
            create = data_preprocessing_and_pridiction(audioFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (WavFileException e) {
            throw new RuntimeException(e);
        }
        if(create) {
            return true;
        }
        else{
            File audioFile = new File(audioFilePath);
            audioFile.delete();
            return false;
        }
    }
    private boolean data_preprocessing_and_pridiction(String wav_path) throws IOException, WavFileException {
        double spectrum[] = dataPreprocessing.spectrumprocesing(wav_path);
        float meanMFCCValues[][] = dataPreprocessing.mfccprocesing(spectrum);
        boolean isCheck = loadModdelANDprediction(meanMFCCValues);
        return isCheck;
    }
    private boolean loadModdelANDprediction(float [][] meanMFCC) throws IOException {
        MappedByteBuffer tflitemodel = FileUtil.loadMappedFile(this, "converted_model_4layer.tflite");
        Interpreter tflite;

        Interpreter.Options tfliteOption = new Interpreter.Options();
        tfliteOption.setNumThreads(1);
        tflite = new Interpreter(tflitemodel, tfliteOption);

        int imageTensorIndex = 0;
        int [] imgeShape = tflite.getInputTensor(imageTensorIndex).shape();
        DataType imgeDataType = tflite.getInputTensor(imageTensorIndex).dataType();
        int probabilityTensorIndex = 0;
        int probabilityShape[] = tflite.getOutputTensor(probabilityTensorIndex).shape();
        DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

        ByteBuffer inputBuffer1 = ByteBuffer.allocateDirect(38400).order(ByteOrder.nativeOrder());
        // 1 * 120 * 80 * 1
        for (int j = 0; j < 120; j++) {
            for (int k = 0; k < 80; k++) {
                inputBuffer1.putFloat(meanMFCC[j][k]);
            }
        }
        TensorBuffer outputTensorBuffer = TensorBuffer.createFixedSize(probabilityShape,probabilityDataType);
        tflite.run(inputBuffer1, outputTensorBuffer.getBuffer());
        float[] result = outputTensorBuffer.getFloatArray();
        float nonsound =  result[0];
        float checksound = result[1];
        String non = String.format("%.2f", nonsound);
        String check = String.format("%.2f", checksound);
        String s = non + " " + check;
        //Log.v("확인", Float.toString(sum) + " " + Float.toString(nonsound) + " " + Float.toString(checksound));
        return checksound - nonsound >= 0.2f;
    }
    private float[] ServerRequst(File audioFile){
        final float[] returnsoftmax = new float[6];
        RequestBody requestBody = RequestBody.create(MediaType.parse("audio/wav"), audioFile);
        MultipartBody.Part audioPart = MultipartBody.Part.createFormData("audio", audioFile.getName(), requestBody);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(90, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://15.164.76.29:5000")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        MyApi myApi = retrofit.create(MyApi.class);
        Call<ApiResponse> call = myApi.uploadAudio(audioPart);
        call.enqueue(new Callback<ApiResponse>() {

            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                ApiResponse result = response.body();
                Log.v("서버로 부터 받음", "서버로 받음");
                audioFile.delete();
                String softemax_string = result.getMessage();
                String softmax_arr[] = softemax_string.split(" ");
                for (int i = 0;i<6;i++){
                    returnsoftmax[i] = Float.parseFloat(softmax_arr[i]);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                returnsoftmax[0] = -1.0f;
            }
        });
        return returnsoftmax;
    }
}
