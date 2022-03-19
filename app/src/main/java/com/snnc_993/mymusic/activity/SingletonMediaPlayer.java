package com.snnc_993.mymusic.activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

public class SingletonMediaPlayer {
    private static SingletonMediaPlayer instance;
    private MediaPlayer mp;
    private int buffer_state;

    private SingletonMediaPlayer() {
        Log.d("SMP","Creating new media player");
        this.mp = new MediaPlayer();
        this.buffer_state = 0;
    }

    public static SingletonMediaPlayer getInstance() {
        if (instance == null) {
            instance = new SingletonMediaPlayer();
        }

        return instance;
    }

    public void play(String path, final TextView tv_messaging){
        if(this.mp.isPlaying()){
            Log.d("SMP","Player is playing, now I'll stop and reset it");
            this.mp.stop();
            this.mp.reset();
            this.mp.release();
        }

        Log.d("SMP","Set audio stream type");
        this.mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {

            Log.d("SMP","Set data source");
            this.mp.setDataSource(path);
            Log.d("SMP","Prepare async");
            this.mp.prepareAsync();
            Log.d("SMP","Done!");
            tv_messaging.setText("Connecting to the server...please wait");
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            tv_messaging.setText(e.toString());
            Log.e("SMP","IllegalArgumentException");
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            tv_messaging.setText(e.toString());
            Log.e("SMP","SecurityException");
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            tv_messaging.setText(e.toString());
            Log.e("SMP","IllegalStateException");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            tv_messaging.setText(e.toString());
            Log.e("SMP","IOException");
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            tv_messaging.setText(e.toString());
            Log.e("SMP","Generic Exception");
        }
    }

    public void checkBufferState(final TextView tv_buffer_message){
        final SingletonMediaPlayer self = this;

        Log.d("SMP","  Set on prepared listener");
        this.mp.setOnErrorListener(new MediaPlayer.OnErrorListener(){

            @Override
            public boolean onError(MediaPlayer mp, int arg1, int arg2) {
                // TODO Auto-generated method stub
                return false;
            }

        });
        this.mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mPlayer) {
                MediaPlayer.OnBufferingUpdateListener lis = new MediaPlayer.OnBufferingUpdateListener(){
                    public void onBufferingUpdate(MediaPlayer mPlayer, int percent) {
                        Log.d("SMP","      Mediaplayer ready (preparation done). Inside buffer listener");
                        self.buffer_state = percent;
                        if(tv_buffer_message != null){
                            tv_buffer_message.setText(percent+"%");
                        }
                    }
                };

                Log.d("SMP","    Mediaplayer ready (preparation done). Installing buffer listener");
                mPlayer.setOnBufferingUpdateListener(lis);
                Log.d("SMP","    Mediaplayer ready (preparation done). Starting reproduction");
                mPlayer.start();
                Log.d("SMP","    Mediaplayer ready (preparation done). Done!");
            }
        });
    }

    public int getBufferState(){
        return this.buffer_state;
    }
}
