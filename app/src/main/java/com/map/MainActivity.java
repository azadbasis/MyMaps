package com.map;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10; // code you want.
    private static final String TAG = "MainActivity";
    private static final int POLYGON_POINTS = 3;

    private GoogleMap mMap;
    private static final double
            SEATTLE_LATE = 47.60621,
            SEATTLE_LNG = -122.33207,
            SYDNEY_LAT = -33.867487,
            SYDNEY_LNG = 151.20699,
            NEWYORK_LAT = 40.714353,
            NEWYORK_LNG = -74.005973;

    private GoogleApiClient mLocationClient;
    List<Marker> markers = new ArrayList<>();
    private Polygon shape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (servicesOK()) {
            setContentView(R.layout.activity_map);
            initMap();
            mLocationClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mLocationClient.connect();

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
            gotoLocation(SYDNEY_LAT, SYDNEY_LNG, 15);
            customWindow();

            Toast.makeText(this, "Map connected!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Map not connected! ", Toast.LENGTH_SHORT).show();
        }

    }

    private void customWindow() {
        if (mMap != null) {
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {


                    View v = getLayoutInflater().inflate(R.layout.info_window, null);
                    TextView tvLocality = (TextView) v.findViewById(R.id.tvLocality);
                    TextView tvLat = (TextView) v.findViewById(R.id.tvLat);
                    TextView tvLng = (TextView) v.findViewById(R.id.tvLng);
                    TextView tvSnippet = (TextView) v.findViewById(R.id.tvSnippet);

                    LatLng latLng = marker.getPosition();
                    tvLocality.setText(marker.getTitle());
                    tvLat.setText("Latitude: " + latLng.latitude);
                    tvLng.setText("Longitude: " + latLng.longitude);
                    tvSnippet.setText(marker.getSnippet());

                    return v;


                }
            });
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    Geocoder gc = new Geocoder(MainActivity.this);
                    List<Address> list = null;
                    try {
                        list = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    Address add = list.get(0);
                    MainActivity.this.addMarker(add, latLng.latitude, latLng.longitude);
                }
            });
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    String msg = marker.getTitle() + " (" +
                            marker.getPosition().latitude + ", " +
                            marker.getPosition().longitude + ")";
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Geocoder gc = new Geocoder(MainActivity.this);
                    List<Address> list = null;
                    LatLng ll = marker.getPosition();
                    try {
                        list = gc.getFromLocation(ll.latitude, ll.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    Address add = list.get(0);
                    marker.setTitle(add.getLocality());
                    marker.setSnippet(add.getCountryName());
                    marker.showInfoWindow();
                }
            });
        }
    }

    private void gotoLocation(double lat, double lng, float zoom) {

        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);
    }

    public void geoLocate(View view) throws IOException {

        hideSoftKeyboard(view);
        TextView tv = (TextView) findViewById(R.id.editText1);
        String searchString = tv.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(searchString, 1);

        if (list.size() > 0) {
            Address add = list.get(0);
            String locality = add.getLocality();
            Toast.makeText(this, "Found: " + locality, Toast.LENGTH_SHORT).show();

            double lat = add.getLatitude();
            double lng = add.getLongitude();
            gotoLocation(lat, lng, 15);
            addMarker(add, lat, lng);

        }

    }

    private void addPolygon() {
        PolygonOptions options = new PolygonOptions()
                .fillColor(0x330000FF)
                .strokeWidth(3)
                .strokeColor(Color.BLUE);
        for (int i = 0; i < POLYGON_POINTS; i++) {
            options.add(markers.get(i).getPosition());
        }

        shape = mMap.addPolygon(options);
    }

    private void addMarker(Address add, double lat, double lng) {

        if (markers.size() == POLYGON_POINTS) {
            removeEverything();
        }

        MarkerOptions options = new MarkerOptions()
                .title(add.getLocality())
                .position(new LatLng(lat, lng))
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker());
//              .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker));
        String country = add.getCountryName();
        if (country.length() > 0) {
            options.snippet(country);
        }

        markers.add(mMap.addMarker(options));
        if (markers.size() == POLYGON_POINTS) {
            addPolygon();
        }

    }

    private void removeEverything() {
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
        if (shape != null) {
            shape.remove();
            shape = null;
        }
    }

    private void hideSoftKeyboard(View view) {

        InputMethodManager imm =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Add menu handling code
        switch (id) {
            case R.id.mapTypeNone:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
        }


        return super.onOptionsItemSelected(item);
    }


    public void showCurrentLocation(MenuItem item) {
        if (checkLocationPermission())
            gotoCurrentLocation();
        else
            askPermission();
    }

    private void gotoCurrentLocation() {
        if (checkLocationPermission()) {
            Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
            if (currentLocation == null) {
                Toast.makeText(this, "Couldn't connect!", Toast.LENGTH_SHORT).show();
            } else {
                LatLng latLng = new LatLng(
                        currentLocation.getLatitude(),
                        currentLocation.getLongitude()
                );
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                mMap.animateCamera(update);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "Ready to Map!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // Check for permission to access Location
    private boolean checkLocationPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    // Asks for permissions
    private void askPermission() {
        ActivityCompat.requestPermissions(
                this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
        );
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    gotoCurrentLocation();

                } else {
                    // Permission denied
                    Log.d(TAG, "Fail to get permission PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION");
                }
                break;
            }
        }
    }


}
