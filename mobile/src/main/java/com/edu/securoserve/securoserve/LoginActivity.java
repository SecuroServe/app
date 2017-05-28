package com.edu.securoserve.securoserve;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.edu.securoserve.securoserve.Manifest.permission;
import com.edu.securoserve.securoserve.requests.LoginRequest;
import com.edu.securoserve.securoserve.requests.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import interfaces.ConfirmationMessage;
import library.User;

public class LoginActivity extends AppCompatActivity {

    public  static final int PERMISSIONS_MULTIPLE_REQUEST = 123;

    private EditText username;
    private EditText password;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(username.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Empty values!")
                            .setMessage("Please fill in a valid username/password")
                            .setNeutralButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                } else {
                    try {
                        ConfirmationMessage message = new RetrieveFeedTask().execute(username.getText().toString(), password.getText().toString()).get();

                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private class RetrieveFeedTask extends AsyncTask<String, Void, ConfirmationMessage> {
        @Override
        protected ConfirmationMessage doInBackground(String... params) {
            try {
                LoginRequest loginRequest = new LoginRequest();
                return loginRequest.login(params[0], params[1]);
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final ConfirmationMessage confirmationMessage) {
            super.onPostExecute(confirmationMessage);

            if(confirmationMessage.getStatus().equals(ConfirmationMessage.StatusType.SUCCES)){

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String userToken = (String) confirmationMessage.getReturnObject();

                        UserRequest userRequest = new UserRequest();
                        ConfirmationMessage value = userRequest.getUser(userToken);

                        ObjectMapper mapper = new ObjectMapper();
                        SessionData.getInstance().addValue(SessionData.CURRENT_USER, mapper.convertValue(value.getReturnObject(), User.class));

                        if(SessionData.getInstance().getValue(SessionData.FIREBASE_TOKEN) != null) {
                            ConfirmationMessage message = userRequest.giveUserToken(
                                    ((User)SessionData.getInstance().getValue(SessionData.CURRENT_USER)).getToken(),
                                    (String)SessionData.getInstance().getValue(SessionData.FIREBASE_TOKEN));
                        }

                        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    }
                });
                thread.start();

            } else if(confirmationMessage.getStatus().equals(ConfirmationMessage.StatusType.ERROR)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Oops!")
                        .setMessage("This combination between username and password is wrong")
                        .setNeutralButton("Try Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }
}
