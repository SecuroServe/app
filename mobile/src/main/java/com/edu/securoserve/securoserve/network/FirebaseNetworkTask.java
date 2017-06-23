package com.edu.securoserve.securoserve.network;

import android.content.Context;
import android.os.AsyncTask;

import com.edu.securoserve.securoserve.requests.UserRequest;

import interfaces.ConfirmationMessage;

/**
 * Created by guillaimejanssen on 19/06/2017.
 */

public class FirebaseNetworkTask extends AsyncTask<String, Void, ConfirmationMessage> implements INetworkTask{

    @Override
    protected ConfirmationMessage doInBackground(String... params) {
        UserRequest userRequest = new UserRequest();
        return userRequest.giveUserToken(params[0], params[1]);
    }

    @Override
    protected void onPostExecute(ConfirmationMessage confirmationMessage) {
        super.onPostExecute(confirmationMessage);
    }

    @Override
    public void sendAlert(String title, String message, String buttonText) {

    }
}
