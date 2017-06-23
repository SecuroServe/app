package com.edu.securoserve.securoserve;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.securoserve.securoserve.network.MediaAlertNetworkTask;
import com.edu.securoserve.securoserve.requests.AlertRequest;
import com.edu.securoserve.securoserve.requests.CalamityRequest;
import com.edu.securoserve.securoserve.requests.MediaRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;

import interfaces.ConfirmationMessage;
import library.Alert;
import library.Calamity;
import library.Media;
import library.MediaFile;
import library.User;

public class CalamityActivity extends AppCompatActivity {

    private static final String TAG = CalamityActivity.class.getSimpleName();
    private TextView title;
    private TextView message;
    private ImageButton editContentButton;
    private ImageButton uploadContentButton;
    private ImageView imageView;
    
    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    private Calamity calamity;

    private CalamityRequest calamityRequest;
    private AlertRequest alertRequest;
    private MediaRequest mediaRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calamity);

        this.calamityRequest = new CalamityRequest();
        this.alertRequest = new AlertRequest();
        this.mediaRequest = new MediaRequest();

        title = (TextView) findViewById(R.id.calamityTitle);
        message = (TextView) findViewById(R.id.calamityInformation);
        uploadContentButton = (ImageButton) findViewById(R.id.add_image_button);
        editContentButton = (ImageButton) findViewById(R.id.edit_content_button);
        imageView = (ImageView) findViewById(R.id.testImageView);

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
            public void onClick(View v) {dispatchTakePictureIntent();
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
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap image = (Bitmap) extras.get("data");

            try {
                imageView.setImageBitmap(image);
                new MediaAlertNetworkTask(getApplicationContext(), image, calamity.getId()).execute(
                        ((User)SessionData.getInstance().getValue(SessionData.CURRENT_USER)).getToken(),
                        "Media for calamity@" + System.currentTimeMillis(), "No description available").get();

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }
    }

//    private class RetrieveFeedTask extends AsyncTask<String, Void, ConfirmationMessage> {
//        @Override
//        protected ConfirmationMessage doInBackground(String... params) {
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(final ConfirmationMessage confirmationMessage) {
//            super.onPostExecute(confirmationMessage);
//
//            if(message.getStatus().equals(ConfirmationMessage.StatusType.SUCCES)) {
//
//                Media media = mapper.convertValue(message, Media.class);
//
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray = stream.toByteArray();
//
//                MultipartFile multipartFile = new MockMultipartFile("image", byteArray);
//                ConfirmationMessage m1 = mediaRequest.uploadMedia(userToken, media.getId(), multipartFile);
//
//            } else if (message.getStatus().equals(ConfirmationMessage.StatusType.ERROR)) {
//
//            }
//        }
//    }
}
