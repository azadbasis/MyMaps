package com.map;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapStateManager {
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String ZOOM = "zoom";

    private static final String PREFS_NAME = "mapCameraState";

    private SharedPreferences mapStatePrefs;

    public MapStateManager(Context context) {
        mapStatePrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

}
