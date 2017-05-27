package com.edu.securoserve.securoserve;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import library.Calamity;

public class CalamityActivity extends AppCompatActivity {

    private static final String TAG = CalamityActivity.class.getSimpleName();
    private TextView title;
    private TextView message;
    private ImageButton uploadContentButton;
    
    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri; // file url to store image/video

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calamity);

        title = (TextView) findViewById(R.id.calamityTitle);
        message = (TextView) findViewById(R.id.calamityInformation);
        uploadContentButton = (ImageButton) findViewById(R.id.add_content_button);


        final Calamity calamity = (Calamity) getIntent().getSerializableExtra("CALAMITY");
        title.setText(calamity.getTitle());
        message.setText(calamity.getMessage());
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
