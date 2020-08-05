package com.example.sttmmessenger;

import android.graphics.Color;

public class OneMessage {
        public String picture;
        public String text;
        public int pos;
        public String voice_text;
        public String file;
        public String voice;
        public String filereso;
        public int col;
        public int textcol;

    public OneMessage(String picture, String text, int pos, String voice_text, String file, String voice, String filereso,int col, int textcol) {
        this.picture = picture;
        this.text = text;
        this.pos = pos;
        this.voice_text = voice_text;
        this.file = file;
        this.voice = voice;
        this.filereso = filereso;
        this.col=col;
        this.textcol=textcol;

    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getVoice_text() {
        return voice_text;
    }

    public void setVoice_text(String voice_text) {
        this.voice_text = voice_text;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getFilereso() {
        return filereso;
    }

    public void setFilereso(String filereso) {
        this.filereso = filereso;
    }
    public void setCol(int col){
        this.col=col;
    }
    public int  getCol(){
        return col;
    }

    public int getTextcol() {
        return textcol;
    }
}
