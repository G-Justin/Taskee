package com.ss_salt.android.taskee.packages.models;

public class Item {

    //========================================================================================
    // Properties
    //========================================================================================

    private String name;
    private int value;
    private int weight;

    //========================================================================================
    // Constructors
    //========================================================================================

    public Item(String name, int value, int weight) {
        this.name = name;
        this.value = value;
        this.weight = weight;
    }


    //========================================================================================
    // Accessors
    //========================================================================================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
