package com.example.lzh.test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoFragment extends Fragment {

    final static String ARG_SECTION_NUMBER = "section number";

    private RequestQueue requestQueue;
    

    List<InfoElement> infoData = new ArrayList<>();
    
    private TextView header_address;
    private TextView detail_address;
    private TextView header_phone;
    private TextView detail_phone;
    private TextView header_price;
    private TextView detail_price;
    private TextView header_rating;
    private RatingBar detail_rating;
    private TextView header_google;
    private TextView detail_google;
    private TextView header_url;
    private TextView detail_url;
    
    
    
    

    public InfoFragment(){

    }

    public static InfoFragment newInstance(int sectionNumber) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.info_fragment, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View myView = view;
        
        header_address = (TextView) myView.findViewById(R.id.header_address);
        detail_address = (TextView) myView.findViewById(R.id.detail_address);
        header_phone = (TextView) myView.findViewById(R.id.header_phone);
        detail_phone = (TextView) myView.findViewById(R.id.detail_phone);
        header_price = (TextView) myView.findViewById(R.id.header_price);
        detail_price = (TextView) myView.findViewById(R.id.detail_price);
        header_rating = (TextView) myView.findViewById(R.id.header_rating);
        detail_rating = (RatingBar) myView.findViewById(R.id.detail_rating);
        header_google = (TextView) myView.findViewById(R.id.header_google);
        detail_google = (TextView) myView.findViewById(R.id.detail_google);
        header_url = (TextView) myView.findViewById(R.id.header_url);
        detail_url = (TextView) myView.findViewById(R.id.detail_url);
        
        
        
        
        
        
        
        
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.start();

        String url = "http://cs11134nojsforhw9.us-east-2.elasticbeanstalk.com/detail?placeid=";
        url += PlaceDetailActivity.placeid;

        JsonObjectRequest infoRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    infoData = new ArrayList<>();
                    JSONObject result = response.getJSONObject("result");
                    if (result.has("formatted_address")) {
                        infoData.add(new InfoElement("Address", (String) result.get("formatted_address")));
                    }
                    else{
                        infoData.add(new InfoElement("Address", "No Address"));
                    }
                    if (result.has("international_phone_number")) {
                        infoData.add(new InfoElement("Phone Number", (String) result.get("international_phone_number")));
                    }
                    else{
                        infoData.add(new InfoElement("Phone Number", "No Phone Number"));
                    }
                    if (result.has("price_level")) {
                        String s = "";
                        int n = (int)result.get("price_level");
                        for(int i = 0; i < n; i++){s = s + "$";}
                        infoData.add(new InfoElement("Price Level", s));
                    }
                    else{
                        infoData.add(new InfoElement("Price Level", "No Price Level"));
                    }
                    if (result.has("rating")) {
                        infoData.add(new InfoElement("Rating", String.valueOf(result.get("rating"))));
                    }
                    else{
                        infoData.add(new InfoElement("Rating", "No Rating"));
                    }
                    if (result.has("url")) {
                        infoData.add(new InfoElement("Google Page", (String) result.get("url")));
                    }
                    else{
                        infoData.add(new InfoElement("Google Page", "No Google Pag"));
                    }
                    if (result.has("website")) {
                        infoData.add(new InfoElement("Website", (String) result.get("website")));
                    }
                    else{
                        infoData.add(new InfoElement("Website", "No Website"));
                    }

                    
                    setView(infoData);
                    
                    

                } catch (JSONException e) {

                }
            }
        },  new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );

        requestQueue.add(infoRequest);
    }
    
    public void setView(List<InfoElement> infoData){
        for(int position = 0; position < infoData.size();position++) {
            InfoElement ie = infoData.get(position);

            final String header = ie.getHeader();
            final String detail = ie.getDetail();

            switch(position) {
                case 0:
                    header_address.setText(header);
                    detail_address.setText(detail);
                    break;
                case 1:
                    header_phone.setText(header);
                    detail_phone.setText(detail);
                    if (!detail.equals("No Phone Number")) {
                        detail_phone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri number = Uri.parse("tel:" + detail);
                                getContext().startActivity(new Intent(Intent.ACTION_DIAL, number));

                            }
                        });
                        SpannableString content = new SpannableString(detail);
                        content.setSpan(new UnderlineSpan(), 0, detail.length(), 0);
                        detail_phone.setText(content);
                        detail_phone.setTextColor(Color.RED);
                    }
                    break;
                case 2:
                    header_price.setText(header);
                    detail_price.setText(detail);
                    break;
                case 3:
                    header_rating.setText(header);
                    if(!detail.equals("No Rating")){
                        detail_rating.setNumStars(5);
                        detail_rating.setRating(Float.parseFloat(detail));
                    }
                    break;
                case 4:
                    header_google.setText(header);
                    detail_google.setText(detail);
                    if(!detail.equals("No Google Page")){
                        detail_google.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(detail));
                                getContext().startActivity(i);
                            }
                        });

                        SpannableString content = new SpannableString(detail);
                        content.setSpan(new UnderlineSpan(), 0, detail.length(), 0);
                        detail_google.setText(content);
                        detail_google.setTextColor(Color.RED);
                    }
                    break;
                case 5:
                    header_url.setText(header);
                    detail_url.setText(detail);
                    if(!detail.equals("No Website")) {
                        detail_url.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(detail));
                                getContext().startActivity(i);
                            }
                        });


                        SpannableString content = new SpannableString(detail);
                        content.setSpan(new UnderlineSpan(), 0, detail.length(), 0);
                        detail_url.setText(content);
                        detail_url.setTextColor(Color.RED);
                    }
                    break;
            }

        }
        
    }
    
    
    
    
}










/*
class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {

    List<InfoElement> infoData = new ArrayList<>();
    Context mtext;

    public InfoAdapter(Context text, List<InfoElement> data) {
        mtext = text;
        infoData = data;

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView header, detail;
        public RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            header = (TextView) itemView.findViewById(R.id.info_header);
            detail = (TextView) itemView.findViewById(R.id.info_detail);
            ratingBar = (RatingBar) itemView.findViewById(R.id.inforating);
        }


    }

    public InfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item, parent, false);
        return new InfoAdapter.ViewHolder(v);
    }

    public void onBindViewHolder(InfoAdapter.ViewHolder holder, int position) {
        final ViewHolder myHolder = holder;
        InfoElement ie = infoData.get(position);
        final String header = ie.getHeader();
        final String detail = ie.getDetail();

        header.setText(header);
        detail.setText(detail);


        if(header.equals("Rating") && !detail.equals("No Rating")){

            detail.setText("");
            float rating = Float.parseFloat(detail);
            ratingBar.setRating(rating);


        }



        if(header.equals("Phone Number") && !detail.equals("No Phone Number")) {
            detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri number = Uri.parse("tel:" + detail);
                    mtext.startActivity(new Intent(Intent.ACTION_DIAL, number));

                }
            });
            SpannableString content = new SpannableString(detail);
            content.setSpan(new UnderlineSpan(), 0, detail.length(), 0);
            mydetail.setText(content);
            mydetail.setTextColor(Color.RED);
        }
        if((header.equals("Google Page") && !detail.equals("No Google Page"))
                || (header.equals("Website") && !detail.equals("No Website"))){
            detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(detail));
                    mtext.startActivity(i);
                }
            });
            SpannableString content = new SpannableString(detail);
            content.setSpan(new UnderlineSpan(), 0, detail.length(), 0);
            mydetail.setText(content);
            mydetail.setTextColor(Color.RED);
        }

    }

    @Override
    public int getItemCount() {
        return infoData.size();
    }


}


*/


class InfoElement{

    private String header;

    private String detail;

    public String getHeader(){return header;}

    public String getDetail(){return detail;}

    public InfoElement(String header, String detail){
        this.header = header;
        this.detail = detail;

    }




}

























