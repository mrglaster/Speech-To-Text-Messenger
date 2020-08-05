package com.example.sttmmessenger;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class FriendList extends AppCompatActivity {

    RecyclerView fl;
    GetFriendList frs = new GetFriendList();
   String nick = null;
   OneUser kameraden[] = null;
    ArrayList<OneUser> aras;

    void parser(OneUser[] users){
    if(users!=null){
    aras=new ArrayList<OneUser>(Arrays.asList(users));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        fl.setLayoutManager(linearLayoutManager);
        AvatarAdapter.OnUserClickListener onUserClickListener = new AvatarAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(OneUser user) {
                Intent goAway = new Intent(FriendList.this,Perepiska.class);
                goAway.putExtra("name_sobesednik",user.nickname);
                goAway.putExtra("username",nick);
                startActivity(goAway);
            }
        };
    AvatarAdapter avas = new AvatarAdapter(this,aras,onUserClickListener,nick,1);
    fl.setAdapter(avas);
}}

    class GetFrResp{
        OneUser friends[];

        public String toStringFrs(){
              return "GetFriendsResponse{"+"friends:"+friends+"}";

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    class GetFriendList extends AsyncTask  {

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(kameraden!=null) parser(kameraden);

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
                String action = "get_friendlist";
                char c = '"';

               String data = "{\"action\":" + c + action + c + "," + "\"nickname\":" + c + nick + c + "}";
                try {
                    if(out != null){
                        out.write(data.getBytes());}
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                FriendList.GetFrResp response = null;
                try {
                    response = gson.fromJson(new InputStreamReader(urlConnection.getInputStream()), FriendList.GetFrResp.class);
                    if(response!=null){
                    response.toStringFrs();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(response != null) {
                    urlConnection.disconnect();
                    response.toStringFrs();
                    kameraden = response.friends;

                    return response;

                }else{

                    return 0;}
            }return 0;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            nick = (String) arguments.get("namefr");
        }
        setContentView(R.layout.activity_friend_list);
        fl=findViewById(R.id.friendlist);
        frs.execute();
    }
    public void onExit(View view) throws IOException {
        Intent intent = new Intent(FriendList.this, MainActivity.class);
        startActivity(intent);
    }
    public void onHelp(View view) throws IOException{
        Intent intent = new Intent(FriendList.this, ActivityHelp.class);
        startActivity(intent);
    }
    public void onAlone(View view){
        Intent intent = new Intent(FriendList.this,ActivityFindFriend.class);
        intent.putExtra("username",nick);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        // do nothing
    }
}
