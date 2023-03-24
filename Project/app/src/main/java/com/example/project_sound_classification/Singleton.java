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
        Person person1 = new Person(R.drawable.image1,"차경적",1);
        Person person2 = new Person(R.drawable.image2,"개짓는소리",2);
        Person person3 = new Person(R.drawable.image3,"고양이 울음소리",3);
        Person person4 = new Person(R.drawable.image4,"사이렌",4);
        Person person5 = new Person(R.drawable.image5,"화재경보",5);
        Person person6 = new Person(R.drawable.image6,"도난경보",6);
        Person person7 = new Person(R.drawable.image7,"비상경보",7);
        adapter.addItem(person1);
        adapter.addItem(person2);
        adapter.addItem(person3);
        adapter.addItem(person4);
        adapter.addItem(person5);
        adapter.addItem(person6);
        adapter.addItem(person7);
    }


}
