package com.example.inhataxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        LatLng myPlace = new LatLng( 37.44974447985608, 126.65691823068758);


    }
}
