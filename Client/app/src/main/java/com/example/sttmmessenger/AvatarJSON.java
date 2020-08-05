package com.example.sttmmessenger;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class AvatarJSON {
    @SerializedName("action")
    private String action;
    @SerializedName("nickname")
    private String nickname;

    @SerializedName("image")
    private String encoded_picture;

    public void setAction(String action) {
        this.action = action;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEncoded_picture(String encoded_picture) {
        this.encoded_picture = encoded_picture;
    }
    public String toJson(){
        Gson gson = new Gson(); //библиотека для генерации json
        String jsonResponse = gson.toJson(this);
        Log.i("AVATAR_DATA",jsonResponse);
        return jsonResponse;
    }
}
