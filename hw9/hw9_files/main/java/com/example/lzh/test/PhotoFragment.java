package com.example.lzh.test;

import android.content.Context;
import android.graphics.Bitmap;
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
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PhotoFragment extends Fragment {

    final static String ARG_SECTION_NUMBER = "section number";

    RecyclerView mRecycler;
    RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    GeoDataClient mGeoDataClient;

    List<Bitmap> photoData = new ArrayList<>();

    private TextView noPhoto;

    public PhotoFragment(){

    }

    public static PhotoFragment newInstance(int sectionNumber) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.photo_fragment, container, false);
        return rootView;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View myView = view;

        mRecycler = (RecyclerView) myView.findViewById(R.id.photo_list);
        mLayoutManager = new LinearLayoutManager(getContext());

        String placeid = PlaceDetailActivity.placeid;

        mGeoDataClient = Places.getGeoDataClient(getContext());

        noPhoto = (TextView) myView.findViewById(R.id.noPhoto);

        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeid);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                photoData = new ArrayList<>();
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                final PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                if(photoMetadataBuffer.getCount() == 0){
                    noPhoto.setText("No Photos");
                }
                // Get the first photo in the list.
                for(int i = 0; i < photoMetadataBuffer.getCount();i++) {
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(i);
                    // Get the attribution text.
                    CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);

                    final int n = i;

                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();
                            photoData.add(bitmap);
                            if(n == photoMetadataBuffer.getCount() - 1){
                                mAdapter = new PhotoAdapter(getContext(), photoData);
                                mRecycler.setAdapter(mAdapter);
                                mRecycler.setLayoutManager(mLayoutManager);
                            }
                        }


                    });

                }





            }
        });

    }



}


class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder>{


    List<Bitmap> photoData;
    Context mtext;

    public PhotoAdapter(Context text, List<Bitmap> data) {
        mtext = text;
        photoData = data;

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView photoItem;

        public ViewHolder(View itemView) {
            super(itemView);
            photoItem = (ImageView) itemView.findViewById(R.id.photo_place);
        }


    }

    public PhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
        return new PhotoAdapter.ViewHolder(v);
    }

    public void onBindViewHolder(PhotoAdapter.ViewHolder holder, int position) {
        holder.photoItem.setImageBitmap(photoData.get(position));
    }

    @Override
    public int getItemCount() {
        return photoData.size();
    }







}







