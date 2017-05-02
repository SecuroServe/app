package com.edu.securoserve.securoserve;

import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.springframework.web.client.RestTemplate;

public class LoginActivity extends AppCompatActivity {

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
                    Toast.makeText(getApplicationContext(), "Please fill in a valid username/password!", Toast.LENGTH_SHORT).show();

                } else {
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    //TODO Request to http server
                }
            }
        });
    }
}
