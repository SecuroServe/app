package com.edu.securoserve.securoserve;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.edu.securoserve.securoserve.requests.CalamityRequest;

import library.Calamity;
import library.User;

public class CalamityActivity extends AppCompatActivity {

    private static final String TAG = CalamityActivity.class.getSimpleName();
    private TextView title;
    private TextView message;
    private ImageButton editContentButton;
    private ImageButton uploadContentButton;
    
    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri; // file url to store image/video
    private String m_Text = "";
    private Calamity calamity;

    private CalamityRequest calamityRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calamity);

        this.calamityRequest = new CalamityRequest();

        title = (TextView) findViewById(R.id.calamityTitle);
        message = (TextView) findViewById(R.id.calamityInformation);
        uploadContentButton = (ImageButton) findViewById(R.id.add_image_button);
        editContentButton = (ImageButton) findViewById(R.id.edit_content_button);

        this.calamity = (Calamity) getIntent().getSerializableExtra("CALAMITY");
        title.setText(calamity.getTitle());
        message.setText(calamity.getMessage());

        editContentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTextDialog();
            }
        });

        uploadContentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
                //Intent intent = new Intent(CalamityActivity.this , UploadActivity.class);
                //intent.putExtra("CALAMITY", calamity);
                //startActivity(intent);
            }
        });
    }

    private void createTextDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CalamityActivity.this);
        builder.setTitle("Change calamity information");

        final EditText input = new EditText(CalamityActivity.this);
        input.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        input.setText(calamity.getMessage());
        input.setSingleLine(false);
        input.setLines(8);
        input.setGravity(Gravity.LEFT | Gravity.TOP);
        input.setHorizontalScrollBarEnabled(false);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                calamity.setMessage(input.getText().toString());
                message.setText(input.getText().toString());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        calamityRequest.updateCalamity(((User) SessionData.getInstance().getValue(SessionData.CURRENT_USER)).getToken(),
                                calamity.getId(), calamity.getTitle(), calamity.getMessage(), calamity.getLocation().getId(),
                                calamity.getLocation().getLatitude(), calamity.getLocation().getLongitude(), calamity.getLocation().getRadius(),
                                calamity.getConfirmation(), calamity.getClosed());
                    }
                }).start();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }
}
