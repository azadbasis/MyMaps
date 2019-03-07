package com.map;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class LicenseActivity extends AppCompatActivity {
    String licenseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
//        String licenseText = GooglePlayServicesUtil
//                .getOpenSourceSoftwareLicenseInfo(this);

        TextView tv = (TextView) findViewById(R.id.legal);
        if (licenseText == null) {
            Toast.makeText(LicenseActivity.this,
                    "Google Play services isn't available on this device",
                    Toast.LENGTH_SHORT).show();
        } else {
            tv.setText(licenseText);
        }
    }

}
