package com.edu.securoserve.securoserve;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.edu.securoserve.securoserve.requests.CalamityRequest;
import com.edu.securoserve.securoserve.requests.LoginRequest;
import com.edu.securoserve.securoserve.requests.UserRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import interfaces.ConfirmationMessage;
import library.Calamity;
import library.User;

public class CalamitiesActivity extends AppCompatActivity {

    private Timer timer;
    private ListView assignedCalamity;
    private ListView otherCalamities;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calamities);

        assignedCalamity = (ListView) findViewById(R.id.assignedCalamity);
        assignedCalamity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Calamity calamity = (Calamity) assignedCalamity.getAdapter().getItem(position);

                Intent intent = new Intent();
                intent.setClass(CalamitiesActivity.this, CalamityActivity.class);
                intent.putExtra("CALAMITY", calamity);
                startActivity(intent);
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

        user = (User) SessionData.getInstance().getValue(SessionData.CURRENT_USER);
        List<Calamity> calamity = new ArrayList<>();

        if(user.getAssignedCalamity() != null) {
            calamity.add(user.getAssignedCalamity());
            ArrayAdapter<Calamity> assignedArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, calamity);
            assignedCalamity.setAdapter(assignedArray);
        } else {
            String[] none = {"None assigned"};
            ArrayAdapter<String> assignedArray = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, none);
            assignedCalamity.setAdapter(assignedArray);
        }

        // Refresh calamities each 20 sec
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                new RetrieveFeedTask().execute();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, 10000);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh calamities each 20 sec
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                new RetrieveFeedTask().execute();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, 10000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    private class RetrieveFeedTask extends AsyncTask<Void, Void, ConfirmationMessage> {

        @Override
        protected ConfirmationMessage doInBackground(Void... par) {
            try {
                CalamityRequest calamityRequest = new CalamityRequest();
                return calamityRequest.allCalamity();
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final ConfirmationMessage confirmationMessage) {
            super.onPostExecute(confirmationMessage);

            if(confirmationMessage.getStatus().equals(ConfirmationMessage.StatusType.SUCCES)) {

                Object value = confirmationMessage.getReturnObject();
                ObjectMapper mapper = new ObjectMapper();

                List<Calamity> calamities = mapper.convertValue(value, new TypeReference<List<Calamity>>() {});
                SessionData.getInstance().addValue(SessionData.CALAMITY_LIST, calamities);

                ArrayAdapter<Calamity> otherCalamity = new ArrayAdapter<>(CalamitiesActivity.this, android.R.layout.simple_list_item_1, calamities);
                otherCalamities.setAdapter(otherCalamity);

            } else if (confirmationMessage.getStatus().equals(ConfirmationMessage.StatusType.ERROR)) {
                Toast.makeText(getApplicationContext(), "No calamities found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
