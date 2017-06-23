package com.edu.securoserve.securoserve;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.edu.securoserve.securoserve.network.INetworkTask;
import com.edu.securoserve.securoserve.network.LoginNetworkTask;
import com.edu.securoserve.securoserve.requests.LoginRequest;
import com.edu.securoserve.securoserve.requests.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.ExecutionException;
import interfaces.ConfirmationMessage;
import library.User;

public class LoginActivity extends AppCompatActivity implements INetworkTask {

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
                    sendAlert("Empty values!", "Please fill in a valid username/password", "Continue");

                } else {
                    try {
                        new LoginNetworkTask(getApplicationContext()).execute(username.getText().toString(), password.getText().toString()).get();
                        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));

                    } catch (InterruptedException | ExecutionException e) {
                        Log.e(LoginActivity.this.getLocalClassName(), e.getMessage());
                        sendAlert("Invalid values!", "This combination between username and password is wrong, please try again.", "Try Again");
                    }
                }
            }
        });
    }

    @Override
    public void sendAlert(String title, String message, String buttonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
