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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import interfaces.ConfirmationMessage;
import library.Calamity;
import library.User;

public class AddAlertActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    public static final int PERMISSION_LOCATION_CODE = 99;

    private LocationManager locationManager;
    private boolean locationPermission;
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
                    sendingAlert("Empty values!", "Please fill in a valid title/message", "Continue");

                } else {
                    try {
                        ConfirmationMessage mes = new RetrieveFeedTask().execute(title.getText().toString(), message.getText().toString()).get();
                        if(mes.getStatus().equals(ConfirmationMessage.StatusType.SUCCES)) {
                            title.setText("");
                            message.setText("");
                        }

                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        getLocationPermission();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    private void addMarker() {
        mMap.clear();

        if(lastLocation != null) {

            // Add a marker on user and move the camera
            LatLng user = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(user).title("Your location"));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(user));
        }
    }

    private void sendingAlert(String title, String message, String buttonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddAlertActivity.this);
        builder.setTitle(title)
                .setMessage(message)
                .setNeutralButton(buttonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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
    }


    // LocationListener methods
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
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // Check if it is LOCATION_CODE
        if (requestCode == PERMISSION_LOCATION_CODE) {
            if (grantResults.length == 2 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                locationPermission = true;
                startLocationManager();
            }
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION_CODE);
        } else {
            locationPermission = true;
            startLocationManager();
        }
    }

    private void startLocationManager() {
        if(locationPermission){
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            String provider = LocationManager.GPS_PROVIDER;
            try {
                lastLocation = locationManager.getLastKnownLocation(provider);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);
            } catch(SecurityException e) {
                Log.e("PERMISSION_DENIED", e.getMessage());
            }
        }
    }
}
