package com.edu.securoserve.securoserve;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.edu.securoserve.securoserve.network.LoadCalamitiesNetworkTask;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import library.Calamity;
import library.User;

public class CalamitiesActivity extends AppCompatActivity implements LocationListener {

    private ListView assignedCalamity;
    public ListView otherCalamities;
    private Toolbar toolbar;

    private User user;
    private Timer timer;
    private CalamitiesActivity activity;

    private Filter selectedFilter;

    public static final int PERMISSION_LOCATION_CODE = 99;

    private LocationManager locationManager;
    private Location lastLocation;
    private boolean locationPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calamities);

        // Getting data
        user = (User) SessionData.getInstance().getValue(SessionData.CURRENT_USER);
        List<Calamity> calamity = new ArrayList<>();
        selectedFilter = Filter.ALL_CALAMITIES;

        // Initialize view
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assignedCalamity = (ListView) findViewById(R.id.assignedCalamity);
        assignedCalamity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (assignedCalamity.getItemAtPosition(position).toString().equals("None assigned")) {
                    Toast.makeText(getApplicationContext(), "None calamity assigned!", Toast.LENGTH_SHORT).show();

                } else {
                    Calamity calamity = (Calamity) assignedCalamity.getAdapter().getItem(position);

                    Intent intent = new Intent();
                    intent.setClass(CalamitiesActivity.this, CalamityActivity.class);
                    intent.putExtra("CALAMITY", calamity);
                    startActivity(intent);
                }
            }
        });

        otherCalamities = (ListView) findViewById(R.id.otherCalamity);
        otherCalamities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Calamity calamity = (Calamity) otherCalamities.getAdapter().getItem(position);

                Intent intent = new Intent();
                intent.setClass(CalamitiesActivity.this, CalamityActivity.class);
                intent.putExtra("CALAMITY", calamity);
                startActivity(intent);
            }
        });

        if (user != null && user.getAssignedCalamity() != null) {
            calamity.add(user.getAssignedCalamity());
            ArrayAdapter<Calamity> assignedArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, calamity);
            assignedCalamity.setAdapter(assignedArray);
        } else {
            String[] none = {"None assigned"};
            ArrayAdapter<String> assignedArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, none);
            assignedCalamity.setAdapter(assignedArray);
        }

        getLocationPermission();
        startTimerTask();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimerTask();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }


    private void startTimerTask() {
        this.activity = this;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    new LoadCalamitiesNetworkTask(activity).execute().get();
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(CalamitiesActivity.class.getName(), "Something went wrong with loading the calamities");
                }
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 5000);
    }

    public void changeList() {
        List<Calamity> calamities = (List<Calamity>) SessionData.getInstance().getValue(SessionData.CALAMITY_LIST);

        switch (selectedFilter) {
            case ALL_CALAMITIES:
                otherCalamities.setAdapter(new ArrayAdapter<>(activity.getApplicationContext(), R.layout.custom_text_view, calamities));
                break;

            case YOUR_LOCATION:
                List<Calamity> closeCalamities = new ArrayList<>();

                for (Calamity cal : calamities) {
                    double distance = meterDistanceBetweenPoints(cal.getLocation(), lastLocation);
                    if (distance < 10000.00) {
                        closeCalamities.add(cal);
                    }
                }

                otherCalamities.setAdapter(new ArrayAdapter<>(activity.getApplicationContext(), R.layout.custom_text_view, closeCalamities));
                break;
        }
    }

    private double meterDistanceBetweenPoints(library.Location locationA, Location locationB) {
        Location location1 = new Location("locationA");
        location1.setLatitude(locationA.getLatitude());
        location1.setLongitude(locationA.getLongitude());

        Location location2 = new Location("locationB");
        location2.setLatitude(locationB.getLatitude());
        location2.setLongitude(locationB.getLongitude());

        return location1.distanceTo(location2);
    }


    // LocationListener methods to implement
    @Override
    public void onLocationChanged(Location location) {
        this.lastLocation = location;
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


    // Filter options menu methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_calamities_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:

                final CharSequence[] filter = new CharSequence[]{"All Calamities", "Your location (10km)"};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose your filter option");
                AlertDialog.Builder builder1 = builder.setItems(filter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(filter[which].equals("All Calamities")){
                            selectedFilter = Filter.ALL_CALAMITIES;
                        } else if (filter[which].equals("Your location (10km)")){
                            selectedFilter = Filter.YOUR_LOCATION;
                        }
                        
                        changeList();
                    }
                });
                builder.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Permisisons method
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


    /**
     * Enum for filter options
     */
    public enum Filter {
        ALL_CALAMITIES,
        YOUR_LOCATION
    }
}