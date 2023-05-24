package com.example.project_sound_classification;

public class Soundlist implements Comparable<Soundlist>{
    String name;
    int priority;
    int color;

    public Soundlist(int color, String name, int priority){
        this.name = name;
        this.priority = priority;
        this.color = color;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getColor(){return color;}
    public void setColor(int color) { this.color = color; }

    @Override
    public int compareTo(Soundlist soundlist) {
        return this.priority - soundlist.priority;
    }
}