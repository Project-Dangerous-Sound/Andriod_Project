package com.example.project_sound_classification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.project_sound_classification.databinding.HomeScreenBinding;
import com.example.project_sound_classification.databinding.HomeScreenOneBinding;

import org.json.JSONException;

import org.json.*;
import android.content.res.*;

import java.io.IOException;

public class HomeScreen extends Fragment {

    private HomeScreenOneBinding binding;
    private HomeScreenBinding binding2;
    static Singleton singleton;
    static Json priorityjson;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = HomeScreenOneBinding.inflate(inflater, container, false);
        //binding2 = HomeScreenBinding.inflate(inflater, container, false);
        if (singleton == null) singleton = new Singleton();
        return binding.getRoot();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}