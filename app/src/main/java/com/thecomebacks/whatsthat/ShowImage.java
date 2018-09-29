package com.thecomebacks.whatsthat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.thecomebacks.whatsthat.beans.Answer;
import com.thecomebacks.whatsthat.beans.ImageBean;
import com.thecomebacks.whatsthat.beans.User;
import com.thecomebacks.whatsthat.commons.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ShowImage extends AppCompatActivity {

    private ImageView ivImage;
    private EditText etAnswer;

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

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
