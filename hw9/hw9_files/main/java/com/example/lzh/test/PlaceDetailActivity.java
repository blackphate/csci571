package com.example.lzh.test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PlaceDetailActivity extends AppCompatActivity {

    SectionsPagerAdaptor mSectionsPagerAdaptor;

    public static String placeid;

    private ViewPager mViewPager;

    private JSONObject menuResponse;

    TabLayout detailTab;

    SharedPreferences myprefs;

    private ProgressDialog dialog;

    RequestQueue requestQueue;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);


        String title = getIntent().getStringExtra("name");
        placeid = getIntent().getStringExtra("placeid");
        Log.d("placeid", placeid);
        setTitle(title);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Fetching results");
        dialog.show();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdaptor = new SectionsPagerAdaptor(getSupportFragmentManager());
        mSectionsPagerAdaptor.addFragment(new InfoFragment());
        mSectionsPagerAdaptor.addFragment(new PhotoFragment());
        mSectionsPagerAdaptor.addFragment(new MapFragment());
        mSectionsPagerAdaptor.addFragment(new ReviewFragment());

        detailTab = (TabLayout) findViewById(R.id.place_detail_tabs);

        detailTab.getTabAt(0).setCustomView(R.layout.custom_tab_info);
        detailTab.getTabAt(1).setCustomView(R.layout.custom_tab_photo);
        detailTab.getTabAt(2).setCustomView(R.layout.custom_tab_map);
        detailTab.getTabAt(3).setCustomView(R.layout.custom_tab_review);


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.detail_container);
        mViewPager.setAdapter(mSectionsPagerAdaptor);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.place_detail_tabs);


        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        myprefs = this.getSharedPreferences("Myprefs", MODE_PRIVATE);

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.start();

        String url = "http://cs11134nojsforhw9.us-east-2.elasticbeanstalk.com/detail?placeid=";
        url += PlaceDetailActivity.placeid;

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                menuResponse = response;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonRequest);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        if(myprefs.contains(placeid)) {
            menu.getItem(1).setIcon(R.drawable.heart_fill_white);
        }
        else{
            menu.getItem(1).setIcon(R.drawable.heart_outline_white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        PlaceElement pe = null;
        String name = "";
        String address = "";
        String icon = "";
        String web = "";

        try{
            JSONObject result = menuResponse.getJSONObject("result");
            name = (String) result.get("name");
            address = (String) result.get("vicinity");
            icon = (String) result.get("icon");
            web = "";
            if(result.has("website")){
                web = (String) result.get("website");
            }
            else{
                 web = (String) result.get("url");
            }
            pe = new PlaceElement(icon, name, address, placeid);
        }
        catch(JSONException e){

        }

        if(item.getItemId() == R.id.menu_twitter){
            Intent i = new Intent(Intent.ACTION_VIEW);
            String url = "https://twitter.com/intent/tweet?text=";
            String text = "Check out " + name;
            text += " located at " + address;
            text += " Website: " + web;
            try {
                url += URLEncoder.encode(text, "UTF-8");
            }
            catch(UnsupportedEncodingException e){

            }
            url += "&hashtags=TravelAndEntertainmentSearch";

            i.setData(Uri.parse(url));
            this.startActivity(i);

        }
        if(item.getItemId() == R.id.menu_favorite){
            if(myprefs.contains(placeid)) {
                SharedPreferences.Editor myprefsEditor = myprefs.edit();
                myprefsEditor.remove(placeid);
                myprefsEditor.commit();
                item.setIcon(R.drawable.heart_outline_white);
                Toast.makeText(this, "Favorites removed from the list",Toast.LENGTH_SHORT).show();
            }
            else{
                SharedPreferences.Editor prefsEditor = myprefs.edit();

                Gson gson = new Gson();
                prefsEditor.putString(placeid, gson.toJson(pe).toString());

                prefsEditor.commit();
                item.setIcon(R.drawable.heart_fill_white);
                Toast.makeText(this, "Favorites added to the list",Toast.LENGTH_SHORT).show();
            }
        }




        return super.onOptionsItemSelected(item);
    }





}
