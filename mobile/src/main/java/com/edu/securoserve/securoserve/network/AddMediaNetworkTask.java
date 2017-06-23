package com.edu.securoserve.securoserve.network;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.edu.securoserve.securoserve.requests.MediaRequest;

import interfaces.ConfirmationMessage;
import library.Media;
import library.MediaFile;

/**
 * Created by guillaimejanssen on 19/06/2017.
 */

public class AddMediaNetworkTask extends AsyncTask<String, Void, ConfirmationMessage> implements INetworkTask {

    private Context context;
    private MediaFile media;

    public AddMediaNetworkTask(Context context, MediaFile media) {
        this.context = context;
        this.media = media;
    }

    @Override
    protected ConfirmationMessage doInBackground(String... params) {
        MediaRequest mediaRequest = new MediaRequest();
        return mediaRequest.addMediaFile(params[0], media, Integer.parseInt(params[1]));
    }

    @Override
    protected void onPostExecute(ConfirmationMessage confirmationMessage) {
        super.onPostExecute(confirmationMessage);

        if(confirmationMessage.getStatus().equals(ConfirmationMessage.StatusType.SUCCES)){

            

        } else if (confirmationMessage.getStatus().equals(ConfirmationMessage.StatusType.ERROR)){
            sendAlert("Something went wrong!", "While adding a mediaFile to the server!", "Try again later..");
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
