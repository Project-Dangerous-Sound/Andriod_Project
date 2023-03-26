package com.example.project_sound_classification;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ItemViewHolder>
        implements ItemTouchHelperListener{
    ArrayList<Soundlist> items = new ArrayList<>();
    public ListAdapter(){
    }
    ItemViewHolder holdview;
    List<ItemViewHolder> list = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //LayoutInflater를 이용해서 원하는 레이아웃을 띄워줌
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_priority, parent, false);
        holdview = new ItemViewHolder(view);
        list.add(holdview);
        return holdview;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        //ItemViewHolder가 생성되고 넣어야할 코드들을 넣어준다.
        holder.onBind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public Soundlist get(int index) {return items.get(index);}
    public int getPriority(int index) {return items.get(index).getPriority();}
    public void  setPriority(int index, int toPosition) {this.items.get(index).setPriority(toPosition);}

    public void addItem(Soundlist soundlist){
        items.add(soundlist);
    }

    @Override
    public boolean onItemMove(int from_position, int to_position) {
        //이동할 객체 저장
        Soundlist soundlist = items.get(from_position);
        ItemViewHolder itemViewHolder = list.get(from_position);
        //이동할 객체 삭제
        items.remove(from_position);
        list.remove(from_position);

        //이동하고 싶은 position에 추가
        items.add(to_position, soundlist);
        list.add(to_position, itemViewHolder);
        items.get(to_position).priority = to_position + 1;
        int a = from_position - to_position;
        if (a < 0) {
            items.get(from_position).priority -= 1;
        } else if (a > 0) {
            items.get(to_position + 1).priority += 1;
        }
        for (Soundlist soundlist1 : items) {
            Log.v("확인: ", Integer.toString(soundlist1.priority) + " " + soundlist1.name);
        }
        //Adapter에 데이터 이동알림
        notifyItemMoved(from_position, to_position);
        for (int i = 0; i < items.size(); i++) {
            list.get(i).onBind(items.get(i));
            Log.v("확인: ", list.get(i).list_age.getText() + " " + list.get(i).list_name.getText());
        }
        return true;
    }
    @Override
    public void onItemSwipe(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView list_name, list_age;
        ImageView list_image;


        public ItemViewHolder(View itemView) {
            super(itemView);
            list_name = itemView.findViewById(R.id.list_name);
            list_age = itemView.findViewById(R.id.list_age);
            list_image = itemView.findViewById(R.id.list_image);
        }

        public void onBind(Soundlist sounlist) {
            list_name.setText(sounlist.getName());
            list_age.setText(String.valueOf(sounlist.getPriority()));
            list_image.setImageResource(sounlist.getImage());
        }
    }
}