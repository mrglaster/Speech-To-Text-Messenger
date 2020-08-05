package com.example.sttmmessenger;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ActivityFindFriend extends AppCompatActivity {
    String[] noms;
    String[] avs;
    EditText namepart;
    Button finddahuman;
    RecyclerView recyclerView;
    String nick="";
    String searchablename="";
    String status="";
ArrayList<OneUser> aras;


public void parser(String[] names,String[] avatars){
    if(names!=null&&avatars!=null){
        aras=new ArrayList<OneUser>();
        for (int i = 0; i <names.length ; i++) {
          if(aras!=null)  aras.add(new OneUser(names[i],avatars[i]));
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        AvatarAdapter.OnUserClickListener onUserClickListener = new AvatarAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(OneUser user) {
searchablename=user.nickname;
if(searchablename.equals(nick)) {
    Toast toast = Toast.makeText(getApplicationContext(),
            "Эй! Это же вы! Вы не можете добавить себя в друзья!", Toast.LENGTH_SHORT);
    toast.show();
}else{
    AddUserToFriend usus = new AddUserToFriend();
    usus.execute();
}
            }
        };
        AvatarAdapter avas = new AvatarAdapter(this,aras,onUserClickListener,null,0);
        recyclerView.setAdapter(avas);
    }

}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        Bundle arguments = getIntent().getExtras();
        namepart = findViewById(R.id.namepart);
        finddahuman = findViewById(R.id.searchButton);
        recyclerView = findViewById(R.id.recyclermen);
        if (arguments != null) {
            nick = (String) arguments.get("username");
        }

    }
    public void FindMen(View view){
        if(namepart.getText().length()>0){
            GetUsers gu = new GetUsers();
            gu.execute();
        }
    }
    class GetUsers extends AsyncTask {
        @Override
        protected void onPostExecute(Object o) {
            parser(noms,avs);
            super.onPostExecute(o);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
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
                    e.printStackTrace();
                }

                urlConnection.setDoOutput(true);
                OutputStream out = null;
                try {
                    out = urlConnection.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String action = "find_men";
                char c = '"';
                String data = "{\"action\":" + c + action + c + "," + "\"person\":" + c + namepart.getText().toString() + c + "}";
                try {
                    if (out != null) {
                        out.write(data.getBytes());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                GetUserResponse response = null;
                try {
                    response = gson.fromJson(new InputStreamReader(urlConnection.getInputStream()), GetUserResponse.class);
                    if (response != null) {
                        response.toString();
                        noms=response.names;
                        avs=response.avatars;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (response != null) {
                    urlConnection.disconnect();
                    return response;

                }
            } else {
                return 0;
            }return 0;
        }
    }
    class GetUserResponse{
        String[] names;
        String[]  avatars;
        @Override
        public String toString(){
            return "GetUserResponse{"+"names:"+names+ '\''+",avatars:"+avatars+"}";
        }
    }

    class AddUserToFriend extends AsyncTask{
        @Override
        protected void onPostExecute(Object o) {
            Log.i("STATUS",status);
            if(status.equals("ADDED!")){
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Тепрерь этот пользователь у вас в списке друзей!", Toast.LENGTH_SHORT);
                toast.show();
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Ошибка! Этот пользователь уже есть в  списке ваших друзей!", Toast.LENGTH_SHORT);
                toast.show();
            }
            super.onPostExecute(o);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
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
                    e.printStackTrace();
                }

                urlConnection.setDoOutput(true);
                OutputStream out = null;
                try {
                    out = urlConnection.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String action = "add_friend";
                char c = '"';
                String data = "{\"action\":" + c + action + c + "," + "\"person\":" + c + searchablename + c +","+"\"nickname\":"+c+nick+c +"}";
                try {
                    if (out != null) {
                        out.write(data.getBytes());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                AddResponse response = null;
                try {
                    response = gson.fromJson(new InputStreamReader(urlConnection.getInputStream()), AddResponse.class);
                    if (response != null) {
                        response.toString();
                        status=response.status;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (response != null) {
                    urlConnection.disconnect();
                    return response;
                }
            } else {
                return 0;
            }return 0;
        }
    }
    class AddResponse{
    String status;
    @Override
        public String toString(){
        return "AddFriendResponse{"+"status:"+status+"}";
    }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ActivityFindFriend.this, FriendList.class);
        intent.putExtra("namefr",nick);
        startActivity(intent);
    }

}

