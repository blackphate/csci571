package com.example.lzh.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.ViewHolder>{

    List<PlaceElement> place_detailData;
    Context mtext;
    SearchElement se;

    SharedPreferences myprefs;


    public NearbyAdapter(Context text, List<PlaceElement> data, SearchElement se){
        mtext = text;
        place_detailData = data;
        this.se = se;
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

        final PlaceElement pe = place_detailData.get(position);
        holder.name.setText(pe.getName());
        holder.address.setText(pe.getAddress());
        Picasso.get().load(pe.getIcon()).into(holder.icon);

        final String placeid = pe.getPlaceId();

        if(myprefs.contains(placeid)){
            holder.favorite.setImageResource(R.drawable.heart_fill_red);
        }
        else{
            holder.favorite.setImageResource(R.drawable.heart_outline_black);
        }

        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myprefs.contains(placeid)) {
                    SharedPreferences.Editor myprefsEditor = myprefs.edit();
                    myprefsEditor.remove(placeid);
                    myprefsEditor.commit();
                    myHolder.favorite.setImageResource(R.drawable.heart_outline_black);
                    Toast.makeText(mtext, "Favorites removed from the list",Toast.LENGTH_SHORT).show();
                }
                else{
                    SharedPreferences.Editor prefsEditor = myprefs.edit();

                    Gson gson = new Gson();
                    prefsEditor.putString(placeid, gson.toJson(pe).toString());

                    prefsEditor.commit();
                    myHolder.favorite.setImageResource(R.drawable.heart_fill_red);

                    Toast.makeText(mtext, "Favorites added to the list",Toast.LENGTH_SHORT).show();
                }
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
        return place_detailData.size();
    }




}




















