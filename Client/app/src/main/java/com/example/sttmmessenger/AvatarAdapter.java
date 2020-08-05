package com.example.sttmmessenger;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.ViewHolder>{
    private OnUserClickListener onUserClickListener;
    private LayoutInflater inflater;
    private List<OneUser> avas;
    String nickname;String person="";
    int usus;
    String status="";

    private Context context;
    public AvatarAdapter(Context context, List<OneUser> phones, OnUserClickListener onUserClickListener,String nickname,int usus){
        this.avas=phones;
        this.inflater = LayoutInflater.from(context);
        this.context=context;
        this.onUserClickListener=onUserClickListener;
        this.usus=usus;
        this.nickname=nickname;
    }
    @Override
    public AvatarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.human_show, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(AvatarAdapter.ViewHolder holder, int position) {
        OneUser ava = avas.get(position);
        byte[] decodedString;
        LinearLayout.LayoutParams prms = (LinearLayout.LayoutParams) holder.avatar_pic.getLayoutParams();
        holder.avatar_pic.setLayoutParams(prms);

        if (ava != null && ava.avatar != null) {
            if (ava.avatar.length() > 10) {
                decodedString = Base64.decode(ava.avatar, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.avatar_pic.setImageBitmap(decodedByte);
            } else {
                holder.avatar_pic.setImageResource(R.drawable.avatar_standart);
            }
            int width = 200;
            int height = 200;
            prms.height = height;
            prms.width = width;
            holder.avatar_pic.setLayoutParams(prms);
            holder.username_text.setText(ava.nickname);
            if(usus!=1){
                LinearLayout.LayoutParams button_params = (LinearLayout.LayoutParams) holder.delete.getLayoutParams();
button_params.width=0;
button_params.height=0;
holder.delete.setLayoutParams(button_params);
            }else{
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        person=ava.nickname;
                        DeleteFriendRequest dfr = new DeleteFriendRequest();
                        dfr.execute();
                    }
                });
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
         ImageView avatar_pic;
         TextView username_text;
         Button delete;
         LinearLayout liner;
        ViewHolder(View view){
            super(view);
            delete=view.findViewById(R.id.delete);
            avatar_pic=view.findViewById(R.id.avatar);
            username_text=view.findViewById(R.id.username);
            liner=view.findViewById(R.id.friendlist);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OneUser user =avas.get(getLayoutPosition());
                    onUserClickListener.onUserClick(user);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return avas == null ? 0 : avas.size();
    }

    public interface OnUserClickListener {
        void onUserClick(OneUser user);
    }
    class DeleteResponse{
        String status;
        @Override
        public String toString(){
            return "response{"+"Status:"+status+"}";
        }
    }

    class DeleteFriendRequest extends AsyncTask{
        @Override
        protected void onPostExecute(Object o) {
                Intent intent = new Intent(context, FriendList.class);
                intent.putExtra("namefr",nickname);
               context.startActivity(intent);

            super.onPostExecute(o);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
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
                String action = "delete_friend";
                char c = '"';
                String data = "{\"action\":" + c + action + c + "," + "\"person\":" + c + nickname + c +","+"\"nickname\":"+c+person+c +"}";
                try {
                    if (out != null) {
                        out.write(data.getBytes());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                DeleteResponse response = null;
                try {
                    response = gson.fromJson(new InputStreamReader(urlConnection.getInputStream()), DeleteResponse.class);
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
    }



