package com.example.sttmmessenger;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;
import static com.example.sttmmessenger.avatar.GALLERY_REQUEST;

public class Perepiska extends AppCompatActivity {
    Bitmap bitmap;
    String b64Image="nil";
    String b64file="nil";
    String b64filereso="nil";
    Button imagebtn;
    HashMap<String, String> message;
    RecyclerView recyclerView;
    EditText messg_text;
    private static final int REQUEST_RECORD_AUDIO = 0;
    String voice_path;
    String voic_base64;
    InputStream targetStream = null;
  int length_watcer=0;

    class onSendResponse {
        String status;

        public String onSendResponseString() {
            return "sendResponse{" + "status:" + status + '}';
        }
    }

    class oneMessResponse {
        String status;
        public OneMessage[] Your;
        public OneMessage[] its;
        public String toStringerMessage() {
             return "MessageResponse{" + "status:" + status + '\n' + "yours: " + Your + '\n' + ",its:" + its + ')';
        }
    }

    String nick = null;
    String sobesednik = null;
    ArrayList<OneMessage> messages_its;

    //парсер сообщений для  ListView
    void parser(OneMessage[] its, OneMessage[] your) {
        OneMessage allcombine[] = new OneMessage[its.length + your.length];
        if (its != null) {
            for (int i = 0; i < its.length; i++) {
                its[i].textcol = Color.BLACK;
                allcombine[its[i].getPos() - 1] = its[i];
            }

        }
        if (your != null) {
            for (int i = 0; i < your.length; i++) {
                your[i].textcol = Color.WHITE;
                your[i].col = Color.rgb(48, 213, 200);
                allcombine[your[i].getPos() - 1] = your[i]; //messages_its.add(new OneMessage(your[i].getPicture(),your[i].getText(),42,null,null,null,null,3200456));
            }
        }
        for (int i = 0; i < allcombine.length; i++) {
            if (allcombine[i].text.length() == 0&&allcombine[i].file!="nil"&&allcombine[i].picture!="nil")
                allcombine[i].text = "Голосовое: " + allcombine[i].voice_text + " . Нажмите, чтобы прослушать";
        }
        messages_its = new ArrayList<OneMessage>(Arrays.asList(allcombine));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MessageAdapter messageadapter = new MessageAdapter(this, messages_its);

        if (messages_its != null) Log.i("SIZE", String.valueOf(messages_its.size()));
        recyclerView.setAdapter(messageadapter);

        recyclerView.scrollToPosition(messages_its.size()-1);
        Log.i("SETED", "SUCCESFULL");

    }

    class TimeToSend extends AsyncTask {
        @Override
        protected void onPostExecute(Object o) {
            messg_text.setText("");
            voic_base64="";
            b64file="";
            b64Image="";
            b64filereso="";
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
                Gson gsoner = new Gson();
                String data = gsoner.toJson(message);
                try {
                    if (out != null) {
                        out.write(data.getBytes());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                Perepiska.onSendResponse response = null;
                try {
                    response = gson.fromJson(new InputStreamReader(urlConnection.getInputStream()), Perepiska.onSendResponse.class);
                    if (response != null) {
                        response.onSendResponseString();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return 0;
            }
            return 0;
        }
    }

public void GetFile(View view){
    Intent PickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
    PickerIntent.setType("*/*");
    startActivityForResult(PickerIntent, 666);
}
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    class getMessages extends AsyncTask {
        Perepiska.oneMessResponse response;

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (response != null){
                if(response.its.length+response.Your.length!=length_watcer){ parser(response.its, response.Your);}
                length_watcer=response.its.length+response.Your.length;
        }}
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
                char c = '"';

                String action = "get_mess";

                String gm = "{\"action\":" + c + action + c + "," + "\"nickname\":" + c + nick + c + "," + "\"sobesednik\":" + c + sobesednik + c + "}";
                try {
                    if (out != null) {
                        out.write(gm.getBytes());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                response = null;
                try {
                    response = gson.fromJson(new InputStreamReader(urlConnection.getInputStream()), Perepiska.oneMessResponse.class);
                    if (response != null) {
                        response.toStringerMessage();
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (response != null) {
                    response.toStringerMessage();

                    return response;
                }
            }
            return 0;
        }

    }

   @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendingMess(View view) throws IOException {
        String rand = "biba";
        voice_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/STTM/RecordedAudio" + "/" + rand;
        if (messg_text.getText().length() == 0&&b64file.length()<10&&b64Image.length()<10) {
            AndroidAudioRecorder.with(this)
                    .setFilePath(voice_path)
                    .setColor(ContextCompat.getColor(this, R.color.recorder_bg))
                    .setRequestCode(REQUEST_RECORD_AUDIO)
                    .setSource(AudioSource.MIC)
                    .setChannel(AudioChannel.MONO)
                    .setSampleRate(AudioSampleRate.HZ_48000)
                    .setAutoStart(false)
                    .setKeepDisplayOn(true)
                    .record();

        } else {
            message.put("text", messg_text.getText().toString());
            message.put("voice", "nil");
            message.put("picture", b64Image);
            message.put("voice_text", "nil");
            message.put("file", b64file);
            message.put("filereso", b64filereso);
            TimeToSend tts = new TimeToSend();
            tts.execute();
            b64Image="nil";
            b64file="nil";
            b64filereso="nil";


        }
    }


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        imagebtn=findViewById(R.id.imgbtn);
        message = new HashMap();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perepiska);
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            nick = (String) arguments.get("username");
            sobesednik = (String) arguments.get("name_sobesednik");
            message.put("action", "send_mess");
            message.put("sender", nick);
            message.put("reciver", sobesednik);
            messg_text = findViewById(R.id.messpt);
            recyclerView = (RecyclerView) findViewById(R.id.messages_list);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setBackgroundDrawable(
                        new ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimaryDark)));
            }

            Util.requestPermission(this, Manifest.permission.RECORD_AUDIO);
            Util.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            t.start();

        }
    }
public void GetPic(View view){
    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
    photoPickerIntent.setType("image/*");
    startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
}
    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO:
                if (resultCode == RESULT_OK) {
                    final File initialFile = new File(voice_path);
                    try {
                        targetStream = new DataInputStream(new FileInputStream(initialFile));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (initialFile.exists()) {
                        byte[] fileContent = new byte[0];
                        try {
                            fileContent = FileUtils.readFileToByteArray(initialFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            voic_base64 = encodeFileToBase64Binary(fileContent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        message.put("text", "");
                        message.put("voice", voic_base64);
                        Log.i("B43JS", message.get("voice"));
                        message.put("picture", "nil");
                        message.put("voice_text", "nil");
                        message.put("file", "nil");
                        message.put("filereso", "nil");
                        TimeToSend ts = new TimeToSend();
                        ts.execute();

                    }
                }
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK) {
                    if (resultCode == RESULT_OK) {
                        if (data != null) {
                            Uri selectedImage = data.getData();
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                            byte[] bb = bos.toByteArray();
                            b64Image = android.util.Base64.encodeToString(bb, 0);
                            messg_text.setText("[ИЗОБРАЖЕНИЕ]" + messg_text.getText());

                        }
                    }
                } case 666:
                    if(resultCode==RESULT_OK) {
                        Log.i("RESULT_CODE", "OK");
                        String b64 = "";
if(data!=null) {
    Uri uri = data.getData();
    String filepath = "";
    filepath = getPath(getApplicationContext(), uri);
    Log.d("Picture Path", filepath);
//                        Log.i("FP",filepath);
    File file = new File(filepath);
    byte[] bytes = new byte[0];
    try {
        bytes = loadFile(file);
    } catch (IOException e) {
        e.printStackTrace();
    }
    byte[] encoded = Base64.encodeBase64(bytes);
    b64file = new String(encoded);
    b64filereso = getExtension(filepath);
    if (b64Image.length() < 10)
        messg_text.setText("[ПРИЛОЖЕН ФАЙЛ с расширением ." + b64filereso + ". Нажмите, чтобы сохранить] " + messg_text.getText());
}

                    }
            }
        }


    private static String encodeFileToBase64Binary(byte[] file) throws IOException {
        byte[] encoded = Base64.encodeBase64(file);
        return new String(encoded, StandardCharsets.US_ASCII);
    }


    Thread t=new Thread(){

        @Override
        public void run(){
            while(!isInterrupted()){

                try {
                    Thread.sleep(4000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/STTM/RecordedAudio/biba.wav");
                            if(!file.exists()){
                                if(hasConnection(getApplicationContext())){
                                    getMessages gm = new getMessages();
                                    gm.execute();
                                }else{Toast.makeText(getApplicationContext(), "Нет подключения к интернету", Toast.LENGTH_SHORT).show();}}}
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int)length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        is.close();
        return bytes;
    }
    public  String getPath(Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Ошибка загрузки файла! Попробуйте ещё раз!", Toast.LENGTH_SHORT);
            toast.show();
        }
        return result;
    }
    public String getExtension(String filename) {
        return FilenameUtils.getExtension(filename);
    }
    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed(){
        t.interrupt();
        Intent intent = new Intent(Perepiska.this, FriendList.class);
        intent.putExtra("namefr",nick);
        startActivity(intent);
    }
}


