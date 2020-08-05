package com.example.sttmmessenger;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MessageAdapter extends  RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    String randname;
    private LayoutInflater inflater;
    private List<OneMessage> phones;
    byte st;


    MessageAdapter(Context context, List<OneMessage> phones) {
        this.phones = phones;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.custom_messages, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MessageAdapter.ViewHolder holder, int position) {
             OneMessage phone = phones.get(position);
        if(phone!=null){
            final OneMessage mobila=phone;
            holder.textpartview.setText(phone.getText());
            holder.textpartview.setBackgroundColor(phone.getCol());
            holder.textpartview.setTextColor(phone.getTextcol());
            holder.lin.setBackgroundColor(phone.getCol());
if(phone!=null&&phone.picture!=null)if(phone.picture.length()>10){
                LinearLayout.LayoutParams prms=(LinearLayout.LayoutParams)holder.imgmess.getLayoutParams();
                holder.imgmess.setLayoutParams(prms);
                byte[] decodedString = Base64.decode(phone.picture, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,decodedString.length);
                float width=decodedByte.getWidth()/2;
                float height = decodedByte.getHeight()/2;
                holder.imgmess.setImageBitmap(decodedByte);
                prms.width=(int)Math.ceil(width);
                prms.height=(int)Math.ceil(height);
                holder.imgmess.setLayoutParams(prms);

            }else{
               holder.lin.removeView(holder.imgmess);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mobila.voice.length()>10&&mobila.picture.length()<10&&mobila.file.length()<10) {
                        if (st == 0) {
                            st=1;
                            randname = "biba";
                            Toast.makeText(v.getContext(), "Подготовка к воспроизведению...", Toast.LENGTH_SHORT).show();
                            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/STTM/RecordedAudio/biba.wav");
                            byte[] snd = Base64.decode(mobila.voice, 1);
                            try (FileOutputStream fos = new FileOutputStream(file)) {
                                fos.write(snd);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            JThread jh = new JThread();
                            jh.execute();
                        }

                    }
                    if(mobila.file.length()>10&&mobila.picture.length()<10&&mobila.voice.length()<10){
                        String rand = String.valueOf(new Random(50000));
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/STTM/DownloadedFiles/"+rand+"."+mobila.filereso);
                        byte[] snd = Base64.decode(mobila.file, 1);
                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            fos.write(snd);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(v.getContext(), "Сохранено в папку STTM/DownloadedFiles", Toast.LENGTH_SHORT).show();

                    }

                }
            });

        }
    }

    public int getItemCount(){
        return phones.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textpartview;
        final LinearLayout lin;
        final ImageView imgmess;
        final Button delete;
        ViewHolder(View view) {
            super(view);
            textpartview = (TextView) view.findViewById(R.id.name);
            lin=view.findViewById(R.id.lin);
            imgmess=view.findViewById(R.id.image_mess);
            delete=view.findViewById(R.id.delete);
        }
    }
    class JThread extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            MediaPlayer mp = new MediaPlayer();
            try {
                mp.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/STTM/RecordedAudio/"+randname+".wav");
            } catch (IOException e) {
                e.printStackTrace();
            }
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    st=0;
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/STTM/RecordedAudio/biba.wav");
                    if(file.exists()) file.delete();
                }
            });
            try {
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mp.start();
            return null;
        }
    }


}