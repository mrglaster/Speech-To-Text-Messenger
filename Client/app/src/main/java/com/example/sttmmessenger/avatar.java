package com.example.sttmmessenger;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;


public class avatar extends AppCompatActivity {
    String nick;
    byte errsit_l = 0;
    byte non_conl = 0;
    class AvatarResponser{
        String status;
        String image;
        public String toStringAvatar(){
            return "AvatarResponse{" + "status: " + status + '\'' + ",image:" + image + ')';
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    class AvatarRequest extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            if (encoderd_picture != null) {
                String set_server_url = "YOUR ADRESS!";
                URL url1 = null;
                try {
                    url1 = new URL(set_server_url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (url1 != null) {
                    HttpURLConnection urlConnection = null;
                    try {
                        urlConnection = (HttpURLConnection) url1.openConnection();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    urlConnection.setDoOutput(true);
                    OutputStream out = null;
                    try {
                        out = urlConnection.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    AvatarJSON avj =new AvatarJSON();
                    avj.setAction("avatar");
                    avj.setNickname(nick);
                    avj.setEncoded_picture(encoderd_picture);
                    try {
                        if (out != null) {
                            out.write(avj.toJson().getBytes());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    AvatarResponser response = null;
                    try {
                        response = gson.fromJson(new InputStreamReader(urlConnection.getInputStream()), avatar.AvatarResponser.class);
                        if(response!=null) {
                            response.toStringAvatar();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (response != null) {
                        response.toStringAvatar();
                        urlConnection.disconnect();
                        if (response.status != "-1") {

                            Intent intent = new Intent(avatar.this, FriendList.class);
                            intent.putExtra("namefr",nick);
                            startActivity(intent);
                        } else {
                            errsit_l = 1;
                        }
                        return response;
                    } else {
                        non_conl = 1;
                        return 0;
                    }
                }
                return 0;
            }
            return 0;
        }}


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void onClick1(View view){
        if(encoderd_picture!=null){
            AvatarRequest ara = new AvatarRequest();
            Bundle arguments = getIntent().getExtras();
            if(arguments!=null){
                 nick = arguments.get("name").toString();
                 if(nick!=null)Log.i("AVANAME",nick);
            }
            ara.execute();
        }else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Загрузите изображение из галереи или нажмите \"Использовать Стандартный\" ", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    String encoderd_picture;
    Button butt;
    Bitmap bitmap = null;

    @Override
    public void onBackPressed() {
    }





    static final int GALLERY_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        butt = findViewById(R.id.button5);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
        if(butt !=null){butt.setEnabled(false);}

        Button button = findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);


        ImageView imageView = findViewById(R.id.imageView);

        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageView.setImageBitmap(bitmap);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
                    byte[] bb = bos.toByteArray();
                    encoderd_picture =  Base64.encodeToString(bb,0);
                    Logger log = Logger.getLogger(avatar.class.getName());
                    encoderd_picture = '"'+encoderd_picture+'"';
                    if(butt!=null){
                        butt.setEnabled(true);}
                }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void onClickStandart(View view){

            AvatarRequest ara = new AvatarRequest();
            Bundle arguments = getIntent().getExtras();
            if(arguments!=null){
                nick = arguments.get("name").toString();
                if(nick!=null)Log.i("AVANAME",nick);
            }
            encoderd_picture="nil";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            ara.execute();
        }


    }
}
