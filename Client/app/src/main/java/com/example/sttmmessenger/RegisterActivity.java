package com.example.sttmmessenger;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;




class RegisterResponse {
    String status;
    public int token;

    public String toStringer() {

        return "RegisterResponse{" + "status: " + status + '\'' + ",token=" + token + ')';
    }
}

public class RegisterActivity extends AppCompatActivity {



    byte err_sit = 0;
    byte non_con = 0;

    EditText loginr;
    EditText passwordR;
    EditText pinR;

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    class Reger extends AsyncTask {
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
            if (url1 != null) {
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url1.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();}
                    urlConnection.setDoOutput(true);
                    OutputStream out = null;
                    try {
                        out = urlConnection.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String action = "register";
                    char c = '"';
                    String data = "{\"action\":" + c + action + c + "," + "\"nickname\":" + c + nickname + c + "," + "\"password\":" + c + password + c + "," + "\"pin\":" + c + pin + c + "}";

                    try {
                        if (out != null) {
                            out.write(data.getBytes());
                        }
                    }  catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    RegisterResponse response = null;
                    try {
                        response = gson.fromJson(new InputStreamReader(urlConnection.getInputStream()), RegisterResponse.class);
                        response.toStringer();
                    } catch (IOException e1){
                        e1.printStackTrace();
                    }
                    if (response != null) {
                        System.out.println(response.toStringer());
                        urlConnection.disconnect();
                        if (response.token != -1) {
                            Intent intent = new Intent(RegisterActivity.this, avatar.class);
                            intent.putExtra("name",nickname);
                            startActivity(intent);
                        }
                        if (response.token == -1 || response.status == "error") {
                            err_sit = 1;
                        }
                        return response;
                    } else {
                        non_con = 1;

                        return 0;
                    }
                }
                return 0;
            }
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loginr = findViewById(R.id.login);
        passwordR = findViewById(R.id.password);
        pinR = findViewById(R.id.pin);
    }
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void onRegister(View view) throws IOException {
        if (loginr.getText().toString().length() == 0 || passwordR.getText().toString().length() == 0 || pinR.getText().toString().length() == 0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "заполните ВСЕ поля", Toast.LENGTH_SHORT);
            toast.show();

    }else{
            Reger rega = new Reger();

            rega.execute();
            if(err_sit==1){
                Toast toast = Toast.makeText(getApplicationContext(),
                        "ПОЛЬЗОВАТЕЛЬ С ТАКИМ ИМЕНЕМ УЖЕ ЗАРИГЕСТРИРОВАН", Toast.LENGTH_SHORT);
                toast.show();
            }
            if(non_con==1){
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Ошибка соединения с сервером", Toast.LENGTH_SHORT);
                toast.show();
            }

        }}
    public void toLogin(View view){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
