package com.example.fofe.fraud_detection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private Button Challenge;
    private TextView Result;
    private ImageView ImageView1;
    private ImageView ImageView2;
    private TextView DBsignature;
    private TextView FraudSignatute;
    private HorizontalScrollView ListSignature;
    private ImageButton Takepicture;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int GALLERY_IMAGE_CAPTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Challenge = (Button) findViewById(R.id.button);
        Result = (TextView) findViewById(R.id.result);
        ImageView1 = (ImageView) findViewById(R.id.imageView);
        ImageView2 = (ImageView) findViewById(R.id.imageView2);
        DBsignature = (TextView) findViewById(R.id.textView);
        FraudSignatute = (TextView) findViewById(R.id.textView2);
        Takepicture = (ImageButton) findViewById(R.id.imageButton);
        Takepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage(MainActivity.this);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView2.setImageBitmap(imageBitmap);

        }
        if (requestCode == GALLERY_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ImageView2.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(MainActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }




    private void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, GALLERY_IMAGE_CAPTURE);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void ChallengeFunction(){

    }

    public void SelectSignature(View view){
  ImageView  ImageviewTmp = (ImageView)view;
  ImageView1.setImageDrawable(ImageviewTmp.getDrawable());
    }
}