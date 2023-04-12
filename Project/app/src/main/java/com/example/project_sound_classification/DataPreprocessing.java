package com.example.project_sound_classification;

import com.example.project_sound_classification.audiofeature.FFT;
import com.example.project_sound_classification.audiofeature.MFCC;
import com.example.project_sound_classification.librosafeature.WavFile;
import com.example.project_sound_classification.librosafeature.WavFileException;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class DataPreprocessing {

    private MFCC mfcc;

    private int mNumFrames;
    private int mSampleRate;
    private int mChannels;


    public double[] spectrumprocesing(String wav_path) throws IOException, WavFileException {
        File sourceFile = new File(wav_path);
        WavFile wavFile = null;

        wavFile = WavFile.openWavFile(sourceFile);
        mNumFrames = (int) (wavFile.getNumFrames());
        mSampleRate = (int) wavFile.getSampleRate();
        mChannels = wavFile.getNumChannels();

        double[][] buffer = new double[mChannels][mNumFrames];
        int frameOffset = 0;
        int loopCounter = ((mNumFrames * mChannels)/4096) + 1;
        for (int i = 0; i < loopCounter; i++) {
            frameOffset = wavFile.readFrames(buffer, mNumFrames, frameOffset);
        }


        DecimalFormat df = new DecimalFormat("#.#####");
        df.setRoundingMode(RoundingMode.CEILING);

        double [] meanBuffer = new double[mNumFrames];
        for(int q=0;q<mNumFrames;q++){
            double frameVal = 0;
            for(int p=0;p<mChannels;p++){
                frameVal = frameVal + buffer[p][q];
            }
            meanBuffer[q]=Double.parseDouble(df.format(frameVal/mChannels));
        }

        return meanBuffer;
    }

    public float[] mfccprocesing(double[] spectrum){
        mfcc = new MFCC();
        mfcc.setSampleRate(mSampleRate);
        int nMFCC = 120;
        mfcc.setN_mfcc(nMFCC);
        float[] mfccInput = mfcc.process(spectrum);

        int nFFT = mfccInput.length/nMFCC;
        double [][] mfccValues = new double[nMFCC][nFFT];

        //loop to convert the mfcc values into multi-dimensional array
        for(int i=0;i<nFFT;i++){
            int indexCounter = i * nMFCC;
            int rowIndexValue = i%nFFT;
            for(int j=0;j<nMFCC;j++){
                mfccValues[j][rowIndexValue]=mfccInput[indexCounter];
                indexCounter++;
            }
        }
        float [][] test = new float[nMFCC][80];
        //code to take the mean of mfcc values across the rows such that
        //[nMFCC x nFFT] matrix would be converted into
        //[nMFCC x 1] dimension - which would act as an input to tflite model
        float [] meanMFCCValues = new float[nMFCC];
        for(int p=0;p<nMFCC;p++){
            double fftValAcrossRow = 0;
            for(int q=0;q<nFFT;q++){
                fftValAcrossRow = fftValAcrossRow + mfccValues[p][q];
            }
            double fftMeanValAcrossRow = fftValAcrossRow/nFFT;
            meanMFCCValues[p] = (float) fftMeanValAcrossRow;
        }

        return meanMFCCValues;
    }
}