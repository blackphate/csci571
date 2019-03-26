package com.example.lzh.test;

import android.widget.ImageView;

public class PlaceElement {

    private String name, address, icon, placeid;

    public String getName() {return name;}

    public String getAddress() {return address;}

    public String getIcon() {return icon;}

    public String getPlaceId() {return placeid;}

    public void setName(String name) {this.name = name;}

    public void setAddress(String address) {this.address = address;}

    public void setIcon(String icon) {this.icon = icon;}


    public PlaceElement(String icon, String name, String address, String placeid){
        this.icon = icon;
        this.name = name;
        this.address = address;
        this.placeid = placeid;
    }

    public String toString(){

        return "PlaceElement [name=" + name + ", address=" + address + ", icon=" + icon +
                ", placeid=" + placeid + "]";

    }



}
