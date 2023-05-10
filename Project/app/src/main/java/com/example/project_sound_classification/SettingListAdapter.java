package com.example.project_sound_classification;

//https://github.com/skydoves/ColorPickerView#colorpickerdialog

import static android.provider.Settings.System.getString;

import android.content.Context;
import android.content.DialogInterface;
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

import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.sliders.AlphaSlideBar;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class SettingListAdapter extends RecyclerView.Adapter<SettingListAdapter.ItemViewHolder>
{
    private Context mContext;

    ArrayList<Soundlist> items = new ArrayList<>();
    public SettingListAdapter(Context context){
        this.mContext = context;
    }
    /*public SettingListAdapter(){
    }*/
    SettingListAdapter.ItemViewHolder holdview;
    List<SettingListAdapter.ItemViewHolder> list = new ArrayList<>();
    String soundname[] = new String[7];


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
        TextView list_name;
        View list_color;
        ImageView list_image;
        Button setting_btn;
        TextView colorTextView;
        AlphaTileView colorview;
        ColorPickerView colorPickerView;


        public ItemViewHolder(View itemView) {
            super(itemView);
            list_name = itemView.findViewById(R.id.list_name);
            list_color = itemView.findViewById(R.id.color_value);
            list_image = itemView.findViewById(R.id.list_image);

            setting_btn = itemView.findViewById(R.id.change_color_btn);
            colorPickerView = itemView.findViewById(R.id.colorPickerView);

            colorTextView = itemView.findViewById(R.id.textView);
            colorview = itemView.findViewById(R.id.alphaTileView);

            setting_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ColorPickerDialog.Builder(mContext)

                            .setTitle("ColorPicker Dialog")
                            .setPreferenceName("MyColorPickerDialog")
                            .setPositiveButton(mContext.getString(R.string.confirm),
                                    new ColorEnvelopeListener() {
                                        @Override
                                        public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                            Log.v("색상 코드",envelope.getHexCode());
                                            Log.v("인덱스",Integer.toString(getAdapterPosition()));
                                            list_color.setBackgroundColor(envelope.getColor());
                                        }
                                    })
                            .setNegativeButton(mContext.getString(R.string.cancel),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                            .attachAlphaSlideBar(true) // the default value is true.
                            .attachBrightnessSlideBar(true)  // the default value is true.
                            .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                            .show();
                }

            });

        }
        public void onBind(Soundlist sounlist) {
            list_name.setText(sounlist.getName());
            list_color.setBackgroundColor(sounlist.getColor());
            list_image.setImageResource(sounlist.getImage());
        }
    }
}
