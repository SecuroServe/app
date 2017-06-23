package com.edu.securoserve.securoserve.network;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.edu.securoserve.securoserve.DashboardActivity;
import com.edu.securoserve.securoserve.SessionData;
import com.edu.securoserve.securoserve.requests.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import interfaces.ConfirmationMessage;
import library.User;

/**
 * Created by guillaimejanssen on 19/06/2017.
 */

public class UserNetworkTask extends AsyncTask<String, Void, ConfirmationMessage> implements INetworkTask {

    private Context context;
    private ObjectMapper mapper;

    public UserNetworkTask(Context context) {
        this.mapper = new ObjectMapper();
        this.context = context;
    }

    @Override
    protected ConfirmationMessage doInBackground(String... params) {
        UserRequest userRequest = new UserRequest();

        return userRequest.getUser(params[0]);
    }

    @Override
    protected void onPostExecute(ConfirmationMessage confirmationMessage) {
        super.onPostExecute(confirmationMessage);

        SessionData.getInstance().addValue(SessionData.CURRENT_USER, mapper.convertValue(confirmationMessage.getReturnObject(), User.class));

        if(SessionData.getInstance().getValue(SessionData.FIREBASE_TOKEN) != null) {
            new FirebaseNetworkTask().execute(
                    ((User) SessionData.getInstance().getValue(SessionData.CURRENT_USER)).getToken(),
                    (String) SessionData.getInstance().getValue(SessionData.FIREBASE_TOKEN));
        }
    }

    @Override
    public void sendAlert(String title, String message, String buttonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getApplicationContext());
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
