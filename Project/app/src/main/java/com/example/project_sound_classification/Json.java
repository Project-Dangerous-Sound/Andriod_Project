package com.example.project_sound_classification;
import android.content.Context;
import android.content.res.AssetManager;

import java.text.ParseException;
import java.util.*;
import java.io.*;
import org.json.*;
import android.content.res.*;
import android.util.Log;
import android.R;
public class Json{
    Context mcontext;
    String json = "";
    JSONObject jsonObject;
    static String prio[] = {"첫번째", "두번째", "세번째", "네번째", "다섯번째","여섯번째", "일곱번째"};
    private String getJsonString() throws IOException {
        String json = "";

        InputStream is = mcontext.getResources().getAssets().open("Priority.json");
        int fileSize = is.available();
        byte[] buffer = new byte[fileSize];
        is.read(buffer);
        is.close();

        json = new String(buffer, "UTF-8");

        return json;
    }

    public String[] getPriority() throws JSONException{
            String array[] = new String[7];
            int index = 0;
            for (String s : prio) {
                JSONArray jsonArray = jsonObject.getJSONArray(s);
                JSONObject projsonObject = jsonArray.getJSONObject(0);
                array[index] = projsonObject.getString("soundname");
                index++;
            }
            return array;

    }
    public void setPriority(String array[]) throws JSONException, IOException {
        int index = 0;
        Log.v("dasd", "파일 변경");
        for (String s : prio){
            Log.v("변경", array[index]);
            JSONArray jsonArray = jsonObject.getJSONArray(s);
            JSONObject projsonObject = jsonArray.getJSONObject(0);
            projsonObject.remove("soundname");
            projsonObject.put("soundname", array[index]);
            index++;
        }
        index = 0;
        for (String s : prio) {
            JSONArray jsonArray = jsonObject.getJSONArray(s);
            JSONObject projsonObject = jsonArray.getJSONObject(0);
            array[index] = projsonObject.getString("soundname");
            Log.v("변경 후", array[index]);
            index++;
        }
        this.json = getJsonString();
    }
    public Json(Context context) throws JSONException, IOException {
        this.mcontext = context;
        this.json = getJsonString();
        jsonObject = new JSONObject(json);
    }
}
