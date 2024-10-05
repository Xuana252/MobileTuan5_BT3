package com.example.bt3;

import java.io.Serializable;

public class Contact implements Serializable {
    private String id;
    private String number;

    private String name;

    private boolean isSelected = false;

    public Contact (String ID,String NUMBER,String NAME) {
        this.id=ID;
        this.name=NAME;
        this.number=NUMBER;
    }

    public String getId(){
        return this.id;
    }
    public String getNumber(){
        return this.number;
    }
    public String getName(){
        return this.name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}
