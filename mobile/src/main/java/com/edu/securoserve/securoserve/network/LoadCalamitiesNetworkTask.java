package com.edu.securoserve.securoserve.network;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.edu.securoserve.securoserve.CalamitiesActivity;
import com.edu.securoserve.securoserve.R;
import com.edu.securoserve.securoserve.SessionData;
import com.edu.securoserve.securoserve.requests.CalamityRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import interfaces.ConfirmationMessage;
import library.Calamity;

/**
 * Created by guillaimejanssen on 19/06/2017.
 */

public class LoadCalamitiesNetworkTask extends AsyncTask<Void, Void, ConfirmationMessage> implements INetworkTask {

    private ObjectMapper mapper;
    private CalamitiesActivity activity;

    public LoadCalamitiesNetworkTask(CalamitiesActivity activity) {
        this.mapper = new ObjectMapper();
        this.activity = activity;
    }

    @Override
    protected ConfirmationMessage doInBackground(Void... params) {
        CalamityRequest calamityRequest = new CalamityRequest();

        return calamityRequest.allCalamity();
    }

    @Override
    protected void onPostExecute(ConfirmationMessage confirmationMessage) {
        super.onPostExecute(confirmationMessage);

        if(confirmationMessage.getStatus().equals(ConfirmationMessage.StatusType.SUCCES)) {
            List<Calamity> calamities = mapper.convertValue(confirmationMessage.getReturnObject(), new TypeReference<List<Calamity>>() {});
            SessionData.getInstance().clearValue(SessionData.CALAMITY_LIST);
            SessionData.getInstance().addValue(SessionData.CALAMITY_LIST, calamities);
            activity.changeList();

        } else if (confirmationMessage.getStatus().equals(ConfirmationMessage.StatusType.ERROR)) {
            Toast.makeText(activity.getApplicationContext(), "No calamities found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void sendAlert(String title, String message, String buttonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity.getApplicationContext());
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
}
