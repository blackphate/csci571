package com.example.lzh.test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class NearbyActivity extends AppCompatActivity {

    private RequestQueue requestQueue;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<PlaceElement> placeData = new ArrayList<>();

    private Stack<JSONObject> placeStack = new Stack<>();

    private JSONObject currJson;

    private ProgressDialog dialog;

    private Button pre;
    private Button next;

    private TextView noNearby;

    private String next_token;

    private SearchElement se;

    SharedPreferences myprefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_view);
        setTitle("Nearby");

        myprefs = getSharedPreferences("Myprefs", MODE_PRIVATE);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Fetching results");

        mRecyclerView = (RecyclerView) findViewById(R.id.nearby_list);

        mLayoutManager = new LinearLayoutManager(this);



        pre = (Button) findViewById(R.id.previous);
        next = (Button) findViewById(R.id.next);

        noNearby = (TextView) findViewById(R.id.noNearby);

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.start();

        if(myprefs.contains("currjson")){
            String json = myprefs.getString("currjson", "");
            try {
                JSONObject jsonobj = new JSONObject(json);
                setNearbyView(jsonobj);
            }
            catch(JSONException e){

            }
            return;
        }




        final SharedPreferences.Editor myEditor = myprefs.edit();


        String url = "http://csci11134nodejs.us-east-2.elasticbeanstalk.com/";
        String keyword = getIntent().getStringExtra("keyword");
        String distance = getIntent().getStringExtra("distance");
        String category = getIntent().getStringExtra("category");
        double lat = getIntent().getDoubleExtra("lat", -1);
        double lng = getIntent().getDoubleExtra("lng", -1);

        if(keyword.equals("")){
            noNearby.setText("Error");
            return;
        }


        if(distance.equals("")){
            distance = "10";
        }

        url += "nearby?keyword=" + keyword;
        url += "&distance=" + distance;
        url += "&category=" + category;
        url += "&lat=" + lat;
        url += "&lng=" + lng;
        Log.d("url", url);



        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>(){

                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(getApplicationContext(), "succeed", Toast.LENGTH_SHORT).show();
                    setNearbyView(response);
                    myEditor.putString("currjson", response.toString());
                    myEditor.commit();
                }
            },new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {
                    noNearby.setText("Error");
                    Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT);
                }
            });

        requestQueue.add(jsonObjectRequest);
    }


    private void setNearbyView(final JSONObject response){
        dialog.dismiss();
        try {
            placeData = new ArrayList<>();
            currJson = response;
            JSONArray results = response.getJSONArray("results");
            for(int i = 0; i < results.length(); i++){
                JSONObject res = results.getJSONObject(i);
                String name = (String) results.getJSONObject(i).get("name");
                String address = (String) results.getJSONObject(i).get("vicinity");
                String icon = (String) results.getJSONObject(i).get("icon");
                int favorite = R.drawable.heart_outline_black;
                String placeid = (String) results.getJSONObject(i).get("place_id");
                placeData.add(new PlaceElement(icon,name,address,placeid));
            }
            mAdapter = new NearbyAdapter(NearbyActivity.this, placeData, se);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(mLayoutManager);

            if(placeData.size() == 0){
                noNearby.setText("No result");
            }


            if(!placeStack.empty()){
                pre.setEnabled(true);
            }
            else{
                pre.setEnabled(false);
            }

            if(response.has("next_page_token")){
                next.setEnabled(true);
                next_token = (String) response.get("next_page_token");
            }
            else{
                next.setEnabled(false);
            }

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(next_token != null) {
                        String pageUrl = "http://csci11134nodejs.us-east-2.elasticbeanstalk.com/";
                        pageUrl += "page?token=" + next_token;
                        placeStack.push(response);
                        dialog.setMessage("Fetching next page");
                        dialog.show();
                        JsonObjectRequest nextPageRequest = new JsonObjectRequest(Request.Method.GET, pageUrl, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                setNearbyView(response);
                            }
                        },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                        requestQueue.add(nextPageRequest);
                    }
                }
            });

            pre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject json = placeStack.pop();
                    setNearbyView(json);
                }
            });

        }
        catch(JSONException e){
            noNearby.setText("No result");
            Log.d("error", "json error");
        }

    }

}
