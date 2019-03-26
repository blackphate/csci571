package com.example.lzh.test;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class SearchFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    final static String ARG_SECTION_NUMBER = "section number";

    private TextInputEditText keyword;
    private Spinner category;
    private TextInputEditText distance;
    private RadioButton current;
    private RadioButton other;
    private AutoCompleteTextView place;
    private AutoCompleteAdapter mAdapter;
    private Button search;
    private Button clear;
    private ProgressBar searchProgress;
    private ProgressDialog dialog;
    private TextInputLayout keywordLayout;
    private TextInputLayout placeLayout;
    RadioGroup rg;
    Intent nearby;
    private FusedLocationProviderClient mFusedLocationClient;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    Location curr;

    public static double user_lat;
    public static double user_lng;


    protected GeoDataClient mGeoDataClient;



    private RequestQueue requestQueue;

    public SearchFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */

    public static SearchFragment newInstance(int sectionNumber) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_fragment, container, false);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View myView = view;
        category = (Spinner) myView.findViewById(R.id.category);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.CategoryItem, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoryAdapter);

        mGeoDataClient = Places.getGeoDataClient(getContext());

        keyword = (TextInputEditText) myView.findViewById(R.id.keyword);
        distance = (TextInputEditText) myView.findViewById(R.id.distance);
        current = (RadioButton) myView.findViewById(R.id.current);
        other = (RadioButton) myView.findViewById(R.id.other);
        place = (AutoCompleteTextView) myView.findViewById(R.id.other_place);
        search = (Button) myView.findViewById(R.id.search);
        clear = (Button) myView.findViewById(R.id.clear);

        keywordLayout = (TextInputLayout) myView.findViewById(R.id.keywordLayout);
        placeLayout = (TextInputLayout) myView.findViewById(R.id.placeLayout);

        rg = (RadioGroup) myView.findViewById(R.id.radioSearch);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.current:
                        place.setEnabled(false);
                        place.setText("");
                        break;
                    case R.id.other:
                        place.setEnabled(true);
                        place.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                }
            }
        });


        mAdapter = new AutoCompleteAdapter(getContext(), mGeoDataClient);
        place.setAdapter(mAdapter);
        place.setThreshold(0);
        place.setDropDownHeight(500);

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Fetching reuslts");



        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        else{
            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            curr = location;
                            user_lat = curr.getLatitude();
                            user_lng = curr.getLongitude();
                        }
                    }


            });
        }


        search.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                nearby = new Intent(getActivity(), NearbyActivity.class);

                nearby.putExtra("keyword", keyword.getText().toString());
                nearby.putExtra("distance", distance.getText().toString());
                nearby.putExtra("category", category.getSelectedItem().toString());


                //System.out.println(current.isChecked() + " " + other.isChecked());

                if(current.isChecked()){
                    nearby.putExtra("lat", curr.getLatitude());
                    nearby.putExtra("lng",curr.getLongitude());
                    startActivity(nearby);
                };

                if(other.isChecked()){
                    String place_name = place.getText().toString();
                    String url = "http://csci11134nodejs.us-east-2.elasticbeanstalk.com/";
                    url += "geo?place=" + place_name;
                    requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.start();
                    JsonObjectRequest placeJson = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                JSONObject loc = response.getJSONObject("loc");
                                nearby.putExtra("lat", (double) loc.get("lat"));
                                nearby.putExtra("lng", (double) loc.get("lng"));
                                user_lat = (double) loc.get("lat");
                                user_lng = (double) loc.get("lng");
                                startActivity(nearby);
                            }
                            catch (JSONException e){

                            }
                        }

                    },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("error", "error");
                            }
                        });

                    requestQueue.add(placeJson);
                }
                dialog.show();
            }
        });


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword.setText("");
                distance.setText("");
                category.setSelection(0);
                rg.check(R.id.current);
                place.setText("");
                place.setEnabled(false);
            }
        });



        keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = keyword.getText().toString();
                String filter = text.replaceAll("\\s", "");
                if(filter.equals("")){
                    keywordLayout.setHint("Please enter mandatory field");
                    Toast.makeText(getContext(), "Please fix all fields with error",Toast.LENGTH_SHORT).show();
                }
                else{
                    keywordLayout.setHint("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        place.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(place.isEnabled()) {
                    String text = place.getText().toString();
                    String filter = text.replaceAll("\\s", "");
                    if (filter.equals("")) {
                        placeLayout.setHint("Please enter mandatory field");
                        Toast.makeText(getContext(), "Please fix all fields with error", Toast.LENGTH_SHORT).show();
                    } else {
                        placeLayout.setHint("");
                    }
                }
            }


            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(),
                            new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if(location != null){
                                        curr = location;
                                        user_lat = curr.getLatitude();
                                        user_lng = curr.getLongitude();
                                    }
                                }
                            });
                }
                else{
                    Log.d("Permission", "Denied");
                }
            return;
            }

        }
    }





}
























