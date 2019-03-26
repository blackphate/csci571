package com.example.lzh.test;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static java.lang.Math.min;

public class ReviewFragment extends Fragment {

    final static String ARG_SECTION_NUMBER = "section number";

    private RequestQueue requestQueue;

    RecyclerView mRecycler;
    RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Spinner reviewSpinner;
    Spinner sortSpinner;

    private boolean isGoogle = true;

    private boolean noGoogle = false;
    private boolean noYelp = false;
    private TextView noReview;

    List<ReviewElement> googleData = new ArrayList<>();
    List<ReviewElement> yelpData = new ArrayList<>();

    List<JSONObject> google_review = new ArrayList<>();
    List<JSONObject> yelp_review = new ArrayList<>();


    public ReviewFragment(){

    }

    public static ReviewFragment newInstance(int sectionNumber) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.review_fragment, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View myView = view;
        mRecycler = (RecyclerView) myView.findViewById(R.id.review_recycler);
        mLayoutManager = new LinearLayoutManager(getContext());
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.start();


        noReview = myView.findViewById(R.id.noReview);
        reviewSpinner = (Spinner) myView.findViewById(R.id.place_review);
        sortSpinner = (Spinner) myView.findViewById(R.id.place_sort);

        ArrayAdapter<CharSequence> reviewAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.place_review, android.R.layout.simple_spinner_item);
        reviewAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reviewSpinner.setAdapter(reviewAdapter);

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.place_sort, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);



        String url = "http://cs11134nojsforhw9.us-east-2.elasticbeanstalk.com/detail?placeid=";
        url += PlaceDetailActivity.placeid;

        JsonObjectRequest reviewRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    google_review = new ArrayList<>();
                    yelp_review = new ArrayList<>();
                    JSONObject result = response.getJSONObject("result");
                    if(result.has("reviews")){
                        JSONArray googleArray = result.getJSONArray("reviews");
                        for(int i = 0; i < googleArray.length();i++){
                            google_review.add(googleArray.getJSONObject(i));
                        }
                    }

                    setGoogleAdapter(google_review);
                    Log.d("1","2");
                    try {
                        String name = (String) result.get("name");
                        String vic = (String) result.get("vicinity");
                        String url = "http://cs11134nojsforhw9.us-east-2.elasticbeanstalk.com/yelp?name=";
                        url += URLEncoder.encode(name, "UTF-8");
                        vic = vic.substring(0, min(vic.length(), 64));
                        url += "&address1=";
                        url += URLEncoder.encode(vic, "UTF-8");
                        JSONObject city = null, state = null, country = null;
                        JSONArray addressComponents = result.getJSONArray("address_components");
                        for (int i = 0; i < addressComponents.length(); i++) {
                            JSONObject com = addressComponents.getJSONObject(i);
                            JSONArray types = com.getJSONArray("types");
                            String type = (String) types.get(0);
                            if (type.equals("locality")) city = com;
                            if (type.equals("administrative_area_level_1")) state = com;
                            if (type.equals("country")) country = com;
                        }
                        url += "&city=";
                        if (city == null) {
                            url += URLEncoder.encode((String) state.get("long_name"), "UTF-8");
                        } else {
                            url += URLEncoder.encode((String) city.get("long_name"), "UTF-8");
                        }

                        url += "&state=";
                        url += (String) state.get("short_name");
                        url += "&country=";
                        url += (String) country.get("short_name");

                        Log.d("yelp_url", url);
                        JsonObjectRequest yelpRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if(response.has("reviews")) {
                                        JSONArray yelpArray = response.getJSONArray("reviews");
                                        for(int i = 0;i < yelpArray.length();i++){
                                            yelp_review.add(yelpArray.getJSONObject(i));
                                        }
                                    }

                                    final ArrayList<JSONObject> google_clone = arrayClone(google_review);
                                    final ArrayList<JSONObject> yelp_clone = arrayClone(yelp_review);
                                    reviewSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            switch(position){
                                                case 0:
                                                    setGoogleAdapter(google_clone);
                                                    isGoogle = true;
                                                    break;
                                                case 1:
                                                    setYelpAdapter(yelp_clone);
                                                    isGoogle = false;
                                                    break;
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });

                                    sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            switch(position){
                                                case 0:
                                                   if(isGoogle) setGoogleAdapter(google_review);
                                                   else setYelpAdapter(yelp_review);
                                                case 1:
                                                    Collections.sort(google_clone, new GoogleRatingComparator());
                                                    Collections.reverse(google_clone);
                                                    Collections.sort(yelp_clone, new YelpRatingComparator());
                                                    Collections.reverse(yelp_clone);
                                                    if(isGoogle) setGoogleAdapter(google_clone);
                                                    else setYelpAdapter(yelp_clone);
                                                    break;
                                                case 2:
                                                    Collections.sort(google_clone, new GoogleRatingComparator());
                                                    Collections.sort(yelp_clone, new YelpRatingComparator());
                                                    if(isGoogle) setGoogleAdapter(google_clone);
                                                    else setYelpAdapter(yelp_clone);
                                                    break;
                                                case 3:
                                                    Collections.sort(google_clone, new GoogleTimeComparator());
                                                    Collections.reverse(google_clone);
                                                    Collections.sort(yelp_clone, new YelpTimeComparator());
                                                    Collections.reverse(yelp_clone);
                                                    if(isGoogle) setGoogleAdapter(google_clone);
                                                    else setYelpAdapter(yelp_clone);
                                                    break;
                                                case 4:
                                                    Collections.sort(google_clone, new GoogleTimeComparator());
                                                    Collections.sort(yelp_clone, new YelpTimeComparator());
                                                    if(isGoogle) setGoogleAdapter(google_clone);
                                                    else setYelpAdapter(yelp_clone);
                                                    break;
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });


                                }
                                catch(JSONException e){
                                    noYelp = true;
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });

                        requestQueue.add(yelpRequest);
                    }

                    catch(UnsupportedEncodingException e){
                        Log.d("encode_error", "error");

                    }









                } catch (JSONException e) {
                    noGoogle = true;
                    e.printStackTrace();
                }
            }
        },  new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );

        requestQueue.add(reviewRequest);
    }

    private void setGoogleAdapter(List<JSONObject> data){

        noReview.setText("");
        googleData = new ArrayList<>();
        for(int i = 0; i < data.size();i++){
            try {
                JSONObject curr = data.get(i);
                String icon = (String) curr.get("profile_photo_url");
                String name = (String) curr.get("author_name");
                int rating = (int) curr.get("rating");
                int time = (int) curr.get("time");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                c.set(1970, 0, 1,0,0);
                c.add(Calendar.SECOND, time);
                String time_text = format.format(c.getTime());
                String text = (String) curr.get("text");
                String url = (String) curr.get("author_url");
                googleData.add(new ReviewElement(icon,name,rating,time_text,text,url));
            }
            catch(JSONException e){

            }
        }
        mAdapter = new ReviewAdapter(getContext(),googleData);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(mLayoutManager);

        if(data.size() == 0)    {
            noReview.setText("No Reviews");
            return;
        }

    }

    private void setYelpAdapter(List<JSONObject> data){
        noReview.setText("");
        yelpData = new ArrayList<>();
        String icon = "";
        for(int i = 0; i < data.size();i++){
            try {
                JSONObject curr = data.get(i);
                JSONObject user = curr.getJSONObject("user");
                try {
                    icon = (String) user.get("image_url");
                }
                catch (Exception e){

                }
                String name = (String) user.get("name");
                int rating = (int) curr.get("rating");
                String time_text = (String) curr.get("time_created");
                String text = (String) curr.get("text");
                String url = (String) curr.get("url");
                yelpData.add(new ReviewElement(icon,name,rating,time_text,text,url));
            }
            catch(JSONException e){

            }
        }
        mAdapter = new ReviewAdapter(getContext(),yelpData);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(mLayoutManager);

        if(data.size() == 0) {
            noReview.setText("No results");
            return;
        }

    }


    private ArrayList<JSONObject> arrayClone(List<JSONObject> data){
        ArrayList<JSONObject> clone = new ArrayList<>();
        for(JSONObject json:data){
            clone.add(json);
        }
        return clone;
    }



}











class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    List<ReviewElement> reviewData = new ArrayList<>();
    Context mtext;

    public ReviewAdapter(Context text, List<ReviewElement> data) {
        mtext = text;
        reviewData = data;

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, time, text;
        public RatingBar rating;
        public ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.review_name);
            time = (TextView) itemView.findViewById(R.id.review_time);
            rating = (RatingBar) itemView.findViewById(R.id.review_rating);
            text = (TextView) itemView.findViewById(R.id.review_text);
            icon = (ImageView) itemView.findViewById(R.id.review_image);
        }


    }

    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewAdapter.ViewHolder(v);
    }

    public void onBindViewHolder(ReviewAdapter.ViewHolder holder, int position) {
        ReviewElement ie = reviewData.get(position);
        holder.name.setText(ie.getName());
        holder.rating.setRating(ie.getRating());
        holder.time.setText(ie.getDate());
        holder.text.setText(ie.getText());
        if(!ie.getIcon().equals("")) {
            Picasso.get().load(ie.getIcon()).into(holder.icon);
        }
        final String user_url = ie.getUrl();
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(user_url));
                mtext.startActivity(i);
            }
        });

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(user_url));
                mtext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewData.size();
    }


}





class ReviewElement{

    private String name, icon, url, date, text;
    private int rating;

    public String getName() {return name;}

    public String getUrl() {return url;}

    public String getIcon() {return icon;}

    public int getRating() {return rating;}

    public String getDate() {return date;}

    public String getText() {return text;}


    public ReviewElement(String icon, String name, int rating, String date, String text, String url){
        this.icon = icon;
        this.name = name;
        this.rating = rating;
        this.date = date;
        this.text = text;
        this.url = url;
    }


}


class GoogleRatingComparator implements Comparator<JSONObject>{

    public int compare(JSONObject j1, JSONObject j2) {

        int r1 = 0;
        int r2 = 0;

        try{
            r1 = (int) j1.get("rating");
            r2 = (int) j2.get("rating");
        }

        catch(JSONException e){

        }

        return r1 - r2;
    }
}


class YelpRatingComparator implements Comparator<JSONObject>{

    public int compare(JSONObject j1, JSONObject j2) {

        int r1 = 0;
        int r2 = 0;

        try{
            r1 = (int) j1.get("rating");
            r2 = (int) j2.get("rating");
        }

        catch(JSONException e){

        }

        return r1 - r2;
    }
}

class GoogleTimeComparator implements Comparator<JSONObject>{

    public int compare(JSONObject j1, JSONObject j2){

        int r1 = 0;
        int r2 = 0;

        try{
            r1 = (int) j1.get("time");
            r2 = (int) j2.get("time");
        }

        catch(JSONException e){

        }

        return r1 - r2;
    }


}

class YelpTimeComparator implements Comparator<JSONObject>{

    public int compare(JSONObject j1, JSONObject j2){

        String r1 = "";
        String r2 = "";

        try{
            r1 = (String) j1.get("time_created");
            r2 = (String) j2.get("time_created");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            try {
                Date date1 = format.parse(r1);
                Date date2 = format.parse(r2);
                if(date1.before(date2)){
                    return -1;
                }
                else return 1;

            }
            catch(ParseException e){

            }

        }

        catch(JSONException e){

        }

        return 0;
    }


}
















