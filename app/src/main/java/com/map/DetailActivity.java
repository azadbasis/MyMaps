package com.map;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int DIALOG_REQUEST = 9001;

    private GoogleMap mMap;
    private Hotel hotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String city = getIntent().getStringExtra("city");
        setTitle(getString(R.string.landon_hotel) + ", " + city);
        hotel = DataProvider.hotelMap.get(city);

        if (hotel == null) {
            Toast.makeText(this, getString(R.string.error_find_hotel) + ": "
                    + city, Toast.LENGTH_SHORT).show();
            return;
        }

        if (servicesOK()) {

            setContentView(R.layout.activity_detail_with_map);
            SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
            Toast.makeText(this, "Ready to Map", Toast.LENGTH_SHORT).show();
        } else {
            setContentView(R.layout.activity_detail);
            int imageResource = getResources().getIdentifier(
                    hotel.getImage(), "drawable", getPackageName());

            ImageView iv = (ImageView) findViewById(R.id.imageView);
            iv.setImageResource(imageResource);


        }



        TextView cityText = (TextView) findViewById(R.id.cityText);
        cityText.setText(hotel.getCity());

        TextView neighborhoodText = (TextView) findViewById(R.id.neighborhoodText);
        neighborhoodText.setText(hotel.getNeighborhood());

        TextView descText = (TextView) findViewById(R.id.descriptionText);
        descText.setText(hotel.getDescription() + "\n");

    }

    private void gotoLocation(Hotel hotel) {
        Geocoder gc = new Geocoder(this);
        List<Address> list;
        try {
            list = gc.getFromLocationName(hotel.getAddress(), 1);
            Address address = list.get(0);
            double lat = address.getLatitude();
            double lng = address.getLongitude();
            LatLng latLong = new LatLng(lat, lng);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLong, 15);
            mMap.moveCamera(update);
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.error_finding_hotel), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(this.getLocalClassName(), e.getMessage());
        }
    }

    public boolean servicesOK() {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(result)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(result, this, DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, getString(R.string.error_connect_to_services), Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap == null) {
            mMap = googleMap;
            if (servicesOK()&&(mMap!=null)) {
                gotoLocation(hotel);
            }
            Toast.makeText(this, "Map connected!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Map not connected!", Toast.LENGTH_SHORT).show();
        }
    }
}
