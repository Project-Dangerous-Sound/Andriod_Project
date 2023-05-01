package com.example.project_sound_classification;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

public class SettingListAdapter  extends RecyclerView.Adapter<SettingListAdapter.ItemViewHolder>
{
    ArrayList<Soundlist> items = new ArrayList<>();
    public SettingListAdapter(){

    }
    SettingListAdapter.ItemViewHolder holdview;
    List<SettingListAdapter.ItemViewHolder> list = new ArrayList<>();
    String soundname[] = new String[7];

    Button setting_btn;
    int defaultColor;

    @NonNull
    @Override
    public SettingListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //LayoutInflater를 이용해서 원하는 레이아웃을 띄워줌
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_setting, parent, false);
        holdview = new SettingListAdapter.ItemViewHolder(view);
        list.add(holdview);

        return holdview;
    }

    @Override
    public void onBindViewHolder(@NonNull SettingListAdapter.ItemViewHolder holder, int position) {
        //ItemViewHolder가 생성되고 넣어야할 코드들을 넣어준다.
        holder.onBind(items.get(position));
        holder.setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    public Soundlist get(int index) {return items.get(index);}


    public void addItem(Soundlist soundlist){
        items.add(soundlist);
    }



    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView list_name, list_color;
        ImageView list_image;
        Button setting_btn;

        public ItemViewHolder(View itemView) {
            super(itemView);
            list_name = itemView.findViewById(R.id.list_name);
            list_color = itemView.findViewById(R.id.color_value);
            list_image = itemView.findViewById(R.id.list_image);
        }
        public void onBind(Soundlist sounlist) {
            list_name.setText(sounlist.getName());
            list_color.setText(String.valueOf(sounlist.getColor()));
            list_image.setImageResource(sounlist.getImage());
        }
    }


}
