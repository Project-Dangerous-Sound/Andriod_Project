package com.example.project_sound_classification;

public class Soundlist {
    int image;
    String name;
    int priority;
    int color;

    public Soundlist(int image, String name, int priority){
        this.image = image;
        this.name = name;
        this.priority = priority;
    }

    public int getImage() {
        return image;
    }
    public void setImage(int image) {
        this.image = image;
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
}