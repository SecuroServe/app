package com.edu.securoserve.securoserve.network;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.edu.securoserve.securoserve.DashboardActivity;
import com.edu.securoserve.securoserve.LoginActivity;
import com.edu.securoserve.securoserve.SessionData;
import com.edu.securoserve.securoserve.requests.LoginRequest;
import com.edu.securoserve.securoserve.requests.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.ExecutionException;

import interfaces.ConfirmationMessage;
import library.User;

/**
 * Created by guillaimejanssen on 19/06/2017.
 */

public class LoginNetworkTask extends AsyncTask<String, Void, ConfirmationMessage> implements INetworkTask {

    private Context context;

    public LoginNetworkTask(Context context) {
        this.context = context;
    }

    @Override
    protected ConfirmationMessage doInBackground(String... params) {
        LoginRequest loginRequest = new LoginRequest();

        return loginRequest.login(params[0], params[1]);
    }

    @Override
    protected void onPostExecute(ConfirmationMessage confirmationMessage) {
        super.onPostExecute(confirmationMessage);

        if(confirmationMessage.getStatus().equals(ConfirmationMessage.StatusType.SUCCES)){
            try {
                new UserNetworkTask(this.context).execute(confirmationMessage.getReturnObject().toString()).get();

            } catch (InterruptedException | ExecutionException e) {
                Log.e(LoginNetworkTask.class.getName(), e.getMessage());
                sendAlert("Something went wrong!", "An error showed up when loading the user.", "Try Again");
            }

        } else if(confirmationMessage.getStatus().equals(ConfirmationMessage.StatusType.ERROR)) {
            sendAlert("Invalid values!", "This combination between username and password is wrong, please try again.", "Try Again");
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
