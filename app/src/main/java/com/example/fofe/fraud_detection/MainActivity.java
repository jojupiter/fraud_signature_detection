package com.example.fofe.fraud_detection;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    protected Interpreter tffite;
    private static final String ModelPath ="detection.tflite";
   // private int[] labelProbArray = {1,2};
    private ArrayList<String> labels = new ArrayList<>();
    private int BATCH_SIZE=1;
    private int MODEL_INPUT=150;
    private int PIXEL_SIZE=3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAndroidVersion();
        labels.add("forg");
        labels.add("genuine");
        Challenge = (Button) findViewById(R.id.button);
        Result = (TextView) findViewById(R.id.result);
        ImageView1 = (ImageView) findViewById(R.id.imageView);
        ImageView2 = (ImageView) findViewById(R.id.imageView2);
        DBsignature = (TextView) findViewById(R.id.textView);
        FraudSignatute = (TextView) findViewById(R.id.textView2);
        Result =(TextView)findViewById(R.id.result);
        Takepicture = (ImageButton) findViewById(R.id.imageButton);
        Takepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    selectImage(MainActivity.this);

            }
        });
        Challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChallengeFunction();
            }
        });

    }



    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions(MainActivity.this);

        } else {
            // code for lollipop and pre-lollipop devices
        }

    }
    private boolean checkAndRequestPermissions(final Activity context) {
        int camera = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        int wtite = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (wtite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("in fragment on request", "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("in fragment on request", "CAMERA & WRITE_EXTERNAL_STORAGE READ_EXTERNAL_STORAGE permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d("in fragment on request", "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            showDialogOK("Camera and Storage Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions(MainActivity.this);
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(MainActivity.this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }
    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
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
        Log.e("INCHALLENGE","challenge run");
        try{

           // float [][] result = new float[1][labels.size()];

            float [][] result = new float[1][1];
            tffite= new Interpreter(loadModelFile(MainActivity.this));
            tffite.run(convertBitmapToBytebuffer(ImageView2),result);
            List<Recognition>l= getSortedResultFloat(result);
            Result.setText(""+l.get(0).probability);

        }catch ( Exception e){
            Log.e("Tensorflow","LoadModelFile error ",e);
        }

    }


    private List<Recognition> getSortedResultFloat(float[][] labelProbArray) {

String g = "genuine";
         ArrayList<Recognition> recognitions = new ArrayList<>();

        for (int i = 0; i < labels.size(); ++i) {
            float confidence = labelProbArray[0][i];
             recognitions.add(new Recognition(g,confidence));
            }


        return recognitions;
    }





    private ByteBuffer convertBitmapToBytebuffer(ImageView Img){
/*
        BitmapDrawable drawable= (BitmapDrawable)Img.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        int bytes= bitmap.getByteCount();
        ByteBuffer buffer= ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buffer);
        byte[] array = buffer.array();
        return buffer;
/*
        BitmapDrawable drawable= (BitmapDrawable)Img.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteBuffer imgData= null;
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Convert the image to floating point.
        int pixel = 0;
        long startTime = SystemClock.uptimeMillis();
        for (int i = 0; i < getImageSizeX(); ++i) {
            for (int j = 0; j < getImageSizeY(); ++j) {
                final int val = intValues[pixel++];
                addPixelValue(val);
            }
        }
        long endTime = SystemClock.uptimeMillis();
        LOGGER.v("Timecost to put values into ByteBuffer: " + (endTime - startTime));

*/

        BitmapDrawable drawable= (BitmapDrawable)Img.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Bitmap.createScaledBitmap(bitmap,150,150,false);
        ByteBuffer imgData= ByteBuffer.allocate(BATCH_SIZE*MODEL_INPUT*MODEL_INPUT*4*PIXEL_SIZE);
        imgData.order(ByteOrder.nativeOrder());
        int[] intValues = new int[150* 150];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < MODEL_INPUT; ++i) {
            for (int j = 0; j < MODEL_INPUT; ++j) {
                final int val = intValues[pixel++];


                    imgData.putFloat((((val >> 16) & 0xFF))/255f);
                    imgData.putFloat((((val >> 8) & 0xFF))/255f);
                    imgData.putFloat((((val) & 0xFF))/255f);


            }
        }
        return imgData;





    }
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(ModelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


    public void SelectSignature(View view){
  ImageView  ImageviewTmp = (ImageView)view;
  ImageView1.setImageDrawable(ImageviewTmp.getDrawable());
    }
}