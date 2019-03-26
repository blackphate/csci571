package com.example.lzh.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class FavoriteFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */


    final static String ARG_SECTION_NUMBER = "section number";

    SharedPreferences myprefs;

    public static Context mtext;

    public static RecyclerView mRecyclerView;
    public static RecyclerView.Adapter mAdapter;
    public static RecyclerView.LayoutManager mLayoutManager;

    List<PlaceElement> favoriteData = new ArrayList<>();


    public FavoriteFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FavoriteFragment newInstance(int sectionNumber) {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.favorite_fragment, container, false);
        mtext = getContext();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myprefs = getActivity().getSharedPreferences("Myprefs", MODE_PRIVATE);

        final View myView = view;

        mRecyclerView = (RecyclerView) myView.findViewById(R.id.favorite_recycler);
        mLayoutManager = new LinearLayoutManager(getContext());

        favoriteData = new ArrayList<>();
        Map<String,?> allEntries = myprefs.getAll();
        for(Map.Entry<String, ?> entry :allEntries.entrySet()){
            String json = (String) entry.getValue();
            try {
                JSONObject jsonData = new JSONObject(json);
                String name = (String) jsonData.get("name");
                String address = (String) jsonData.get("address");
                String icon = (String) jsonData.get("icon");
                String placeid = (String) jsonData.get("placeid");
                favoriteData.add(new PlaceElement(icon, name, address, placeid));
            }
            catch(JSONException e){

            }
        }

        mAdapter = new FavoriteAdapter(mtext, favoriteData);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    @Override
    public void onResume() {
        super.onResume();
        favoriteData = new ArrayList<>();
        Map<String,?> allEntries = myprefs.getAll();
        for(Map.Entry<String, ?> entry :allEntries.entrySet()){
            String json = (String) entry.getValue();
            try {
                JSONObject jsonData = new JSONObject(json);
                String name = (String) jsonData.get("name");
                String address = (String) jsonData.get("address");
                String icon = (String) jsonData.get("icon");
                String placeid = (String) jsonData.get("placeid");
                favoriteData.add(new PlaceElement(icon, name, address, placeid));
            }
            catch(JSONException e){

            }
        }

        mAdapter = new FavoriteAdapter(mtext, favoriteData);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);



    }
}



class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder>{

    List<PlaceElement> favoriteData;
    Context mtext;

    SharedPreferences myprefs;


    public FavoriteAdapter(Context text, List<PlaceElement> data){
        mtext = text;
        favoriteData = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name, address;
        public ImageView icon, favorite;
        public LinearLayout ll;

        public ViewHolder(View itemView){
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.place_name);
            address = (TextView) itemView.findViewById(R.id.place_address);
            icon = (ImageView) itemView.findViewById(R.id.place_icon);
            favorite = (ImageView) itemView.findViewById(R.id.place_fav);
            ll = (LinearLayout) itemView.findViewById(R.id.place_layout);
        }


    }


    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_list_item, parent, false);
        return new ViewHolder(v);
    }

    public void onBindViewHolder(ViewHolder holder, int position){

        myprefs = mtext.getSharedPreferences("Myprefs", MODE_PRIVATE);

        final ViewHolder myHolder = holder;

        final PlaceElement pe = favoriteData.get(position);
        holder.name.setText(pe.getName());
        holder.address.setText(pe.getAddress());
        Picasso.get().load(pe.getIcon()).into(holder.icon);

        final String placeid = pe.getPlaceId();

        holder.favorite.setImageResource(R.drawable.heart_fill_red);
        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor myprefsEditor = myprefs.edit();
                myprefsEditor.remove(placeid);
                myprefsEditor.commit();
                setUpdatedLayout();
            }
        });

        final String name = pe.getName();
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent place_detail = new Intent(mtext, PlaceDetailActivity.class);
                place_detail.putExtra("name", name);
                place_detail.putExtra("placeid", placeid);
                mtext.startActivity(place_detail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteData.size();
    }

    public void setUpdatedLayout(){

        favoriteData = new ArrayList<>();

        Map<String,?> allEntries = myprefs.getAll();
        for(Map.Entry<String, ?> entry :allEntries.entrySet()){
            String json = (String) entry.getValue();
            try {
                JSONObject jsonData = new JSONObject(json);
                String name = (String) jsonData.get("name");
                String address = (String) jsonData.get("address");
                String icon = (String) jsonData.get("icon");
                String placeid = (String) jsonData.get("placeid");
                favoriteData.add(new PlaceElement(icon, name, address, placeid));
            }
            catch(JSONException e){

            }
        }

        FavoriteFragment.mAdapter = new FavoriteAdapter(mtext, favoriteData);
        FavoriteFragment.mRecyclerView.setAdapter(FavoriteFragment.mAdapter);
        FavoriteFragment.mRecyclerView.setLayoutManager(FavoriteFragment.mLayoutManager);


    }


}






























