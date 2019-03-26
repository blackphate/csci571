package com.example.lzh.test;

public class SearchElement {

    String keyword;
    String distance;
    String category;
    double lat;
    double lng;

    public String getKeyword(){return keyword;}
    public String getDistance(){return distance;}
    public String getCategory(){return category;}
    public double getLat(){return lat;}
    public double getLng(){return lng;}

    public SearchElement(String keyword, String distance, String category, double lat, double lng){

        this.keyword = keyword;
        this.distance = distance;
        this.category = category;
        this.lat = lat;
        this.lng = lng;

    }


}
