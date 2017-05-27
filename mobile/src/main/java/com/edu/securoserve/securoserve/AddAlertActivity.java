package com.edu.securoserve.securoserve;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.edu.securoserve.securoserve.requests.AlertRequest;
import com.edu.securoserve.securoserve.requests.CalamityRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.concurrent.ExecutionException;

import interfaces.ConfirmationMessage;
import library.Calamity;
import library.User;

public class AddAlertActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    public static final int MY_PERMISSION_REQUEST_LOCATION = 99;

    private LocationManager locationManager;
    private String provider;
    private Location lastLocation;

    private GoogleMap mMap;
    private EditText title;
    private EditText message;
    private Button sendAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alert);

        title = (EditText) findViewById(R.id.alertTitle);
        message = (EditText) findViewById(R.id.alertMessage);
        sendAlert = (Button) findViewById(R.id.sendAlertBtn);
        sendAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(title.getText().toString().isEmpty() || message.getText().toString().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddAlertActivity.this);
                    builder.setTitle("Empty values!")
                            .setMessage("Please fill in a valid title/message")
                            .setNeutralButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                } else {
                    try {
                        ConfirmationMessage mes = new RetrieveFeedTask().execute(title.getText().toString(), message.getText().toString()).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
            return;
        }
        lastLocation = locationManager.getLastKnownLocation(provider);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(checkLocationPermission()) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(provider, 1000, 1, this);
            }
        }
    }


    private class RetrieveFeedTask extends AsyncTask<String, Void, ConfirmationMessage> {

        @Override
        protected ConfirmationMessage doInBackground(String... par) {
            try {

                User user = (User) SessionData.getInstance().getValue(SessionData.CURRENT_USER);
                AlertRequest request = new AlertRequest();
                return request.addAlert(user.getToken(), par[0], par[1], 1, lastLocation.getLatitude(), lastLocation.getLongitude(), 300.00);

            } catch (Exception e) {
                Log.e("AddAlertActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final ConfirmationMessage confirmationMessage) {
            super.onPostExecute(confirmationMessage);

            if(confirmationMessage.getStatus().equals(ConfirmationMessage.StatusType.SUCCES)) {

            } else if (confirmationMessage.getStatus().equals(ConfirmationMessage.StatusType.ERROR)) {
                Toast.makeText(getApplicationContext(), "Alert could not be uploaded", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addMarker();
    }

    private void addMarker() {
        mMap.clear();

        // Add a marker on user and move the camera
        LatLng user = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(user).title("Your location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(user));
    }

    public boolean checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSION_REQUEST_LOCATION: {

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(provider, 1000, 1 ,this);
                    }

                } else {

                }
                return;
            }
        }
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        this.lastLocation = location;
        addMarker();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        this.provider = provider;
    }

    @Override
    public void onProviderDisabled(String provider) {
        this.provider = null;
    }
}
