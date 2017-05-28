package com.edu.securoserve.securoserve;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.edu.securoserve.securoserve.requests.UserRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import interfaces.ConfirmationMessage;
import library.User;

/**
 * Created by guillaimejanssen on 23/05/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {

        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        UserRequest userRequest = new UserRequest();
        SessionData.getInstance().addValue(SessionData.FIREBASE_TOKEN, token);

        if(SessionData.getInstance().getValue(SessionData.CURRENT_USER) != null) {
            ConfirmationMessage message = userRequest.giveUserToken(
                    ((User)SessionData.getInstance().getValue(SessionData.CURRENT_USER)).getToken(),
                    (String)SessionData.getInstance().getValue(SessionData.FIREBASE_TOKEN));
        }
    }
}
