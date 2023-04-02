package com.example.project_sound_classification;

import org.json.JSONException;

import java.io.IOException;

public interface ItemTouchHelperListener {
    boolean onItemMove(int from_position, int to_position) throws JSONException, IOException;
    void onItemSwipe(int position);

}