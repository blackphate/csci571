package com.example.lzh.test;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment{

    final static String ARG_SECTION_NUMBER = "section number";

    RequestQueue requestQueue;

    private GoogleMap googleMap;
    MapView mMapView;

    AutoCompleteTextView map_from;
    Spinner map_mode;
    GeoDataClient mGeoDataClient;
    AutoCompleteAdapter mAdapter;

    private double des_lat;
    private double des_lng;
    private String from_text;
    private String mode_text;

    Polyline map_line;


    public MapFragment(){

    }

    public static MapFragment newInstance(int sectionNumber) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_fragment, container, false);
        return rootView;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View myView = view;

        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.start();

        mMapView = (MapView) myView.findViewById(R.id.place_map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        mGeoDataClient = Places.getGeoDataClient(getContext());
        map_from = (AutoCompleteTextView) myView.findViewById(R.id.map_from);
        mAdapter = new AutoCompleteAdapter(getContext(), mGeoDataClient);
        map_from.setAdapter(mAdapter);
        map_from.setThreshold(0);

        map_mode = (Spinner) myView.findViewById(R.id.map_spinner);

        ArrayAdapter<CharSequence> modeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.map_mode, android.R.layout.simple_spinner_item);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        map_mode.setAdapter(modeAdapter);
        map_mode.setSelection(0);
        mode_text = "Driving";

        String url = "http://cs11134nojsforhw9.us-east-2.elasticbeanstalk.com/detail?placeid=";
        url += PlaceDetailActivity.placeid;

        Log.d("url_map", url);
        JsonObjectRequest mapRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject loc = response.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
                    des_lat = (double) loc.get("lat");
                    des_lng = (double) loc.get("lng");

                    Log.d("lat", String.valueOf(des_lat));
                    Log.d("lng", String.valueOf(des_lng));

                    try {
                        MapsInitializer.initialize(getContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mMapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap mMap) {
                            googleMap = mMap;


                            // For dropping a marker at a point on the Map
                            LatLng loc = new LatLng(des_lat, des_lng);
                            googleMap.addMarker(new MarkerOptions().position(loc).title("Marker Title"));

                            // For zooming automatically to the location of the marker
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(14).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            map_from.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    from_text = map_from.getText().toString();
                                    setMap();
                                }
                            });


                            map_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    AutocompletePrediction item = mAdapter.getItem(position);
                                    CharSequence pri_text = item.getPrimaryText(null);
                                    from_text = pri_text.toString();
                                    //CharSequence sec_text = item.getSecondaryText(null);
                                    setMap();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });



                        }
                    });

                } catch (JSONException e) {
                    Log.d("error", "error");
                }
            }
        },  new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(mapRequest);
    }

    private void setMap(){

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=";
        try {
            url += URLEncoder.encode(from_text, "UTF-8");
        }
        catch(UnsupportedEncodingException e){

        }
        url += "&destination=" + des_lat +"," +des_lng;
        url += "&mode=" + mode_text.toLowerCase();
        url += "&key=AIzaSyBX-2M4rVMh_8V-cPkh5fvtb2DrNTCa-t4";
        Log.d("direction_url", url);
        final PolylineOptions polypoints = new PolylineOptions();
        polypoints.clickable(true);
        if(map_line != null) map_line.remove();
        googleMap.clear();
        JsonObjectRequest directionRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    JSONObject routes = response.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
                    JSONObject start_pos = routes.getJSONObject("start_location");
                    JSONObject end_pos = routes.getJSONObject("end_location");
                    LatLng start_latlng = new LatLng((double) start_pos.get("lat"), (double) start_pos.get("lng"));
                    LatLng end_latlng = new LatLng((double) end_pos.get("lat"), (double) end_pos.get("lng"));
                    builder.include(start_latlng);
                    builder.include(end_latlng);
                    polypoints.add(start_latlng);
                    JSONArray steps = routes.getJSONArray("steps");
                    for(int i = 0; i < steps.length();i++){
                        JSONObject way_pos = steps.getJSONObject(i).getJSONObject("end_location");
                        LatLng way_latlng = new LatLng((double) way_pos.get("lat"), (double) way_pos.get("lng"));
                        builder.include(way_latlng);
                        polypoints.add(way_latlng);
                    }
                    polypoints.add(end_latlng);


                    map_line = googleMap.addPolyline(polypoints);
                    map_line.setColor(Color.BLUE);
                    googleMap.addMarker(new MarkerOptions().position(start_latlng));
                    googleMap.addMarker(new MarkerOptions().position(end_latlng));

                    LatLngBounds bounds = builder.build();
                    final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                    googleMap.animateCamera(cu);

                }
                catch(JSONException e){

                }
            }
        }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

        requestQueue.add(directionRequest);
    }


}
   
    



    
    

