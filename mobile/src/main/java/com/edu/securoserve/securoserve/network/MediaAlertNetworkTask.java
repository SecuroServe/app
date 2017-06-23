package com.edu.securoserve.securoserve.network;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.edu.securoserve.securoserve.SessionData;
import com.edu.securoserve.securoserve.requests.AlertRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.ExecutionException;

import interfaces.ConfirmationMessage;
import library.Alert;
import library.Media;
import library.MediaFile;
import library.User;

/**
 * Created by guillaimejanssen on 19/06/2017.
 */

public class MediaAlertNetworkTask extends AsyncTask<String, Void, ConfirmationMessage> implements INetworkTask {

    private ObjectMapper mapper;
    private Context context;

    private Bitmap image;
    private int calamityId;

    public MediaAlertNetworkTask(Context context, Bitmap image, int id) {
        this.mapper = new ObjectMapper();
        this.image = image;
        this.calamityId = id;
    }

    @Override
    protected ConfirmationMessage doInBackground(String... params) {
        AlertRequest alertRequest = new AlertRequest();
        return alertRequest.addAlertToCalamity(params[0], params[1], params[2], 0, 0.00, 0.00, 0.00, this.calamityId);
    }

    @Override
    protected void onPostExecute(ConfirmationMessage confirmationMessage) {
        super.onPostExecute(confirmationMessage);

        if(confirmationMessage.getStatus().equals(ConfirmationMessage.StatusType.SUCCES)) {
            Alert alert = mapper.convertValue(confirmationMessage.getReturnObject(), Alert.class);

            try {

                MediaFile media = new MediaFile(-1, "Foto calamiteit",
                        alert.getCalamityId() + "_" + System.currentTimeMillis(),
                        MediaFile.FileType.PHOTO);

                new AddMediaNetworkTask(this.context, media).execute(
                        ((User) SessionData.getInstance().getValue(SessionData.CURRENT_USER)).getToken(),
                        "" + alert.getId()).get();

            } catch (InterruptedException | ExecutionException e) {
                Log.e(MediaAlertNetworkTask.class.getName(), e.getMessage());
            }

        } else if (confirmationMessage.getStatus().equals(ConfirmationMessage.StatusType.ERROR)) {
            sendAlert("Something went wrong!", "While loading the alert something went wrong", "Try again later..");
            Log.e(MediaAlertNetworkTask.class.getName(), confirmationMessage.getMessage());
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
