package com.map;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int ERROR_DIALOG_REQUEST = 9001;
    GoogleMap mMap;
    private static final double
            SEATTLE_LATE = 47.60621,
            SEATTLE_LNG = -122.33207,
            SYDNEY_LAT = -33.867487,
            SYDNEY_LNG = 151.20699,
            NEWYORK_LAT = 40.714353,
            NEWYORK_LNG = -74.005973;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (servicesOK()) {
            setContentView(R.layout.activity_map);
            initMap();
            Toast.makeText(this, "Ready to Map!", Toast.LENGTH_SHORT).show();

        } else {
            setContentView(R.layout.activity_main);
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public boolean servicesOK() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog =
                    GooglePlayServicesUtil.getErrorDialog(isAvailable, this, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connect to mapping service ", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (mMap == null) {
            mMap = googleMap;
            gotoLocation(SEATTLE_LATE,SEATTLE_LNG,15);
            Toast.makeText(this, "Map connected!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Map not connected! ", Toast.LENGTH_SHORT).show();
        }

    }

    private void gotoLocation(double lat,double lng,float zoom){

        LatLng latLng = new LatLng(lat,lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng,zoom);
        mMap.moveCamera(update);
    }

}
