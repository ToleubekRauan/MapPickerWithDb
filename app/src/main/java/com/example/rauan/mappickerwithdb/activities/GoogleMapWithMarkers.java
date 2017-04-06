package com.example.rauan.mappickerwithdb.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.rauan.mappickerwithdb.R;
import com.example.rauan.mappickerwithdb.datebase.DatabaseConnector;
import com.example.rauan.mappickerwithdb.model.PlacePickModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class GoogleMapWithMarkers extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private List<PlacePickModel> placePickModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map_with_markers);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        this.googleMap.getUiSettings().setCompassEnabled(true);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);

        DatabaseConnector connector = new DatabaseConnector(GoogleMapWithMarkers.this);
        placePickModelList = connector.getPlacesInfo();
        Log.d("listplace", placePickModelList.toString());
        if (placePickModelList != null){
            for (int i = 0; i < placePickModelList.size(); i++) {
                double lat = Double.parseDouble(placePickModelList.get(i).getLatitude());
                double lng = Double.parseDouble(placePickModelList.get(i).getLongitude());
                String title = placePickModelList.get(i).getName();
                LatLng latLng = new LatLng(lat, lng);
                googleMap.addMarker(new MarkerOptions().position(latLng).title(title));
            }
    }

        String latlng = getIntent().getStringExtra("latlng");
        if(latlng!=null) {
            String[] latlong = latlng.split(",");
            double latitude = Double.parseDouble(latlong[0]);
            double longitude = Double.parseDouble(latlong[1]);
            LatLng location = new LatLng(latitude, longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            googleMap.addCircle(new CircleOptions().center(location).radius(100).strokeColor(Color.RED));
            Log.d("datacord", latlng);

        }
    }


    }

