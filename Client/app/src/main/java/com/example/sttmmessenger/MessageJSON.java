package com.example.sttmmessenger;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class MessageJSON {
    @SerializedName("action")
    private String action;
    @SerializedName("sender")
    private String sender;
    @SerializedName("reciver")
    private String reciver;
    @SerializedName("text")
    private String text;
    @SerializedName("picture")
    private String picture;
    @SerializedName("voice")
    private String voice;
    @SerializedName("voice_text")
    private String voice_text;
    @SerializedName("file")
    private String file;
    @SerializedName("filereso")
    private String filereso;
    @SerializedName("pos")
    private int pos;


    public void setAction(String action) {
        this.action = action;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public void setVoice_text(String voice_text) {
        this.voice_text = voice_text;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setFilereso(String filereso) {
        this.filereso = filereso;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }


    public String toJson(){
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(this);
        Log.i("MESSAGE_DATA",jsonResponse);
        return jsonResponse;
    }
}
