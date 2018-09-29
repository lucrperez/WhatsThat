package com.thecomebacks.whatsthat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.thecomebacks.whatsthat.beans.Answer;
import com.thecomebacks.whatsthat.beans.ImageBean;
import com.thecomebacks.whatsthat.beans.User;
import com.thecomebacks.whatsthat.commons.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowImage extends AppCompatActivity {

    private ImageView ivImage;
    private EditText etAnswer;

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    private static final int MY_PERMISSIONS_REQUEST_READ_CAMERA = 100;
    private static final int MY_PERMISSIONS_REQUEST_STORE = 200;

    private static final int TAKE_PICTURE = 10;
    private Uri imageUri;

    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        sp = getSharedPreferences( getApplicationInfo().name, MODE_PRIVATE);

        Intent currentIntet = this.getIntent();

        String username = currentIntet.getStringExtra(Constants.USER_USERNAME);
        int userId = currentIntet.getIntExtra(Constants.USER_ID, -1);

        Button btnLogout = (Button) findViewById(R.id.show_image_btn_logout);
        Button btnPlay = (Button) findViewById(R.id.show_image_btn_play);
        ivImage = (ImageView) findViewById(R.id.img_image_view);
        etAnswer = (EditText) findViewById(R.id.et_image_text);
        Button btnSend = (Button) findViewById(R.id.btn_image_send);
        ImageButton btnInfo = (ImageButton) findViewById(R.id.show_image_btn_info);
        Button btnPoints = (Button) findViewById(R.id.show_image_btn_points);
        ImageButton btnCamera = (ImageButton) findViewById(R.id.show_image_btn_camera);

        // TODO Load image

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowImage.this);
                builder.setTitle(R.string.show_image_ad_logout_title);
                builder.setMessage(R.string.show_image_ad_logout_body);
                builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        spEditor = sp.edit();
                        spEditor.remove(Constants.USER_USERNAME);
                        spEditor.remove(Constants.USER_ID);
                        spEditor.remove(Constants.USER_PASSWORD);
                        spEditor.apply();

                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create();
                builder.show();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answer = etAnswer.getText().toString();
                if (answer != null && !"".equals(answer)) {
                    // TODO send the answer
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.show_image_no_answer, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        btnPlay.setEnabled(false);
        btnPlay.setClickable(false);

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowImage.this);
                builder.setTitle(R.string.how_to_play_title);
                builder.setMessage(R.string.how_to_play_msg);
                builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create();
                builder.show();
            }
        });

        btnPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowImage.this);
                builder.setTitle(R.string.show_image_points_title);
                String msg = getResources().getString(R.string.show_image_points_msg).replace(Constants.MSG_REPLACE_POINTS, String.valueOf(1000));
                builder.setMessage(msg);
                builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create();
                builder.show();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCameraHardware(getApplicationContext())) {
                    checkPermissions();
                }
            }
        });

        StrictMode.VmPolicy.Builder newbuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newbuilder.build());
    }

    private void checkPermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(ShowImage.this, Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(ShowImage.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_CAMERA);
        } else {
            permissionCheck = ContextCompat.checkSelfPermission(ShowImage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(ShowImage.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORE);
            } else {
                /*Intent intent = new Intent();
                intent.setClass(getApplicationContext(), CameraActivity.class);
                startActivity(intent);*/
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photo = getOutputMediaFile(MEDIA_TYPE_IMAGE);

                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photo));
                imageUri = Uri.fromFile(photo);
                startActivityForResult(intent, TAKE_PICTURE);
            }
        }
    }

    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    //getContentResolver().notifyChange(selectedImage, null);
                    //ImageView imageView = (ImageView) findViewById(R.id.ImageView);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    try {
                        bitmap = android.provider.MediaStore.Images.Media
                                .getBitmap(cr, selectedImage);

                        //imageView.setImageBitmap(bitmap);
                        /*Toast.makeText(this, selectedImage.toString(),
                                Toast.LENGTH_LONG).show();*/
                        // TODO convertir bitmap a Base64 y enviar al backend
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissions();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_STORE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissions();
                }
                return;
            }
        }
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private class RetrieveImageRequest extends AsyncTask<User, Void, String> {

        @Override
        protected String doInBackground(User... user) {
            InputStream is = null;
            String response = null;

            try {
                URL url = new URL(Constants.BASE_URL + Constants.GET_IMAGE_URL + String.valueOf(user[0].getId()));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                conn.connect();
                is = conn.getInputStream();

                response = readIt(is);
                //byte[] decodeedString = Base64.decode(base64Response, Base64.DEFAULT);
                //response = BitmapFactory.decodeByteArray(decodeedString, 0, decodeedString.length);

                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (response != null && !"".equals(response)) {
                ImageBean imageBean = ImageBean.getUserFromResponse(response);
                byte[] decodeedString = Base64.decode(imageBean.getEncodedImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodeedString, 0, decodeedString.length);
                ivImage.setImageBitmap(bitmap);
            }

        }

        public String readIt(InputStream stream) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        }
    }

    private class SendAnswer extends AsyncTask<Answer, Void, String> {

        @Override
        protected String doInBackground(Answer... answers) {
            return null;
        }
    }
}
