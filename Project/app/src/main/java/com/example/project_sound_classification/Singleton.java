package com.example.project_sound_classification;

public class Singleton {
    ItemTouchHelperCallback itemTouchHelperCallback;
    ListAdapter adapter;
    public Singleton(){
        createList();
        itemTouchHelperCallback = new ItemTouchHelperCallback(adapter, adapter);
    }

    public void createList(){
        adapter = new ListAdapter();
        Soundlist soundlist1 = new Soundlist(R.drawable.image1,"차경적",1);
        Soundlist soundlist2 = new Soundlist(R.drawable.image2,"개짓는소리",2);
        Soundlist soundlist3 = new Soundlist(R.drawable.image3,"고양이 울음소리",3);
        Soundlist soundlist4 = new Soundlist(R.drawable.image4,"사이렌",4);
        Soundlist soundlist5 = new Soundlist(R.drawable.image5,"화재경보",5);
        Soundlist soundlist6 = new Soundlist(R.drawable.image6,"도난경보",6);
        Soundlist soundlist7 = new Soundlist(R.drawable.image7,"비상경보",7);
        adapter.addItem(soundlist1);
        adapter.addItem(soundlist2);
        adapter.addItem(soundlist3);
        adapter.addItem(soundlist4);
        adapter.addItem(soundlist5);
        adapter.addItem(soundlist6);
        adapter.addItem(soundlist7);
    }


}
