package com.example.sttmmessenger;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    byte errsit_l = 0;
    byte non_conl = 0;
    class LoginResponse {
        String status;
        public int token;

        public String toStringerLogin() {

            return "LoginResponse{" + "status: " + status + '\'' + ",token=" + token + ')';
        }
    }
    EditText login ;
    EditText password ;
    EditText pin ;

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    class Loginer extends AsyncTask {
        EditText loginr;
        EditText passwordR;
        EditText pinR;
        @Override
        protected void onPreExecute() {
            loginr = findViewById(R.id.login);
            passwordR = findViewById(R.id.password);
            pinR = findViewById(R.id.pin);

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String nickname = loginr.getText().toString();
            String password = passwordR.getText().toString();
            String pin = pinR.getText().toString();
            String set_server_url = "http://194.176.114.21:8010";
            URL url1 = null;
            try {
                url1 = new URL(set_server_url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if(url1!= null) {
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
                String action = "login";
                char c = '"';
                String data = "{\"action\":" + c + action + c + "," + "\"nickname\":" + c + nickname + c + "," + "\"password\":" + c + password + c + "," + "\"pin\":" + c + pin + c + "}";
                try {
                    if(out != null){
                        out.write(data.getBytes());}
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
               LoginResponse response = null;
                try {
                    response = gson.fromJson(new InputStreamReader(urlConnection.getInputStream()), LoginResponse.class);
                    response.toStringerLogin();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(response != null){
                    System.out.println(response.toStringerLogin());
                    urlConnection.disconnect();
                    if(response.token != -1&&response.status!="error"){

                        Intent intent = new Intent(MainActivity.this, FriendList.class);
                        intent.putExtra("namefr",nickname);
                        startActivity(intent);}else{errsit_l = 1;
                    }
                    return response;
                }else{
                    non_conl = 1;
                    return 0;}
            }
            return 0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        pin = findViewById(R.id.pin);
        File perp = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/STTM/RecordedAudio/biba.wav");
        if(perp.exists()) perp.delete();
        Util.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/STTM");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File recorded = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/STTM/RecordedAudio");
            if(!recorded.exists()) recorded.mkdirs();
File downloaded_files = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/STTM/DownloadedFiles");
if(!downloaded_files.exists()) downloaded_files.mkdirs();


    }
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void clickJoin(View view)
    {
        if (login.getText().length() == 0 || password.getText().length() == 0 || pin.getText().length() == 0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "заполните ВСЕ поля", Toast.LENGTH_SHORT);
            toast.show();
        }else{
            Intent intent = new Intent(this, FriendList.class);
            intent.putExtra("username", login.getText().toString());
           // startActivity(intent);
            Loginer loger = new Loginer();
            loger.execute();
            if(errsit_l==1){Toast toast = Toast.makeText(getApplicationContext(),
                    "Неправильно введён логин, пароль или pin-код", Toast.LENGTH_SHORT);
                toast.show();}
            if(non_conl==1){Toast toast = Toast.makeText(getApplicationContext(),
                    "Ошибка соединения с сервером", Toast.LENGTH_SHORT);
                toast.show();}
        }

    }
    public void toRegister(View view)
    {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
