package com.hilbing.myplayproject;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.renderscript.RenderScript;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;

import static android.app.Notification.PRIORITY_LOW;
import static com.hilbing.myplayproject.App.CHANNEL_1_ID;

public class PlayerService extends Service {

    MediaPlayer mediaPlayer = new MediaPlayer();

    private NotificationManagerCompat notificationManagerCompat;

    private final IBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {
        PlayerService getService(){
            return PlayerService.this;
        }
    }

    public PlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getStringExtra("url") != null)
            playStream(intent.getStringExtra("url"));

        if(intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)){
            Log.i("INFO", "Start foreground service");
            showNotification();
        }
        else if(intent.getAction().equals(Constants.ACTION.PREV_ACTION)){
            Log.i("INFO", "Previous pressed");
        }
        else if(intent.getAction().equals(Constants.ACTION.PLAY_ACTION)){
            Log.i("INFO", "Play pressed");
            togglePlayer();
        }
        else if(intent.getAction().equals(Constants.ACTION.NEXT_ACTION)){
            Log.i("INFO", "Next pressed");
        }
        else if(intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)){
            Log.i("INFO", "Stop foreground service");
            stopForeground(true);
            stopSelf();
        }

        return START_REDELIVER_INTENT;
    }

    private void showNotification() {

        notificationManagerCompat = NotificationManagerCompat.from(this);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent previousIntent = new Intent(this, PlayerService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent prevPendingIntent = PendingIntent.getActivity(this, 0, previousIntent, 0);

        Intent playIntent = new Intent(this, PlayerService.class);
       // TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
       // stackBuilder.addNextIntent(playIntent);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        Log.i("SERVICE", "PLAY");
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, 0);
                //stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                //PendingIntent.getActivity(this, 0, playIntent, 0);

        Intent nextIntent = new Intent(this, PlayerService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent nextPendingIntent = PendingIntent.getActivity(this, 0, nextIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.bell_icon);

        int playPauseButtonId = android.R.drawable.ic_media_play;
        if(mediaPlayer != null && mediaPlayer.isPlaying())
            playPauseButtonId = android.R.drawable.ic_media_pause;

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID);
        notification.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.bell_icon)
                .setTicker("Channel 1")
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setContentTitle(getResources().getString(R.string.music_player))
                .setContentText(getResources().getString(R.string.my_song))
                .addAction(android.R.drawable.ic_media_previous, "Previous", prevPendingIntent)
                .addAction(playPauseButtonId, "Play", playPendingIntent)
                .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent);

        notificationManagerCompat.notify(1, notification.build());

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void playStream(String url){
        if(mediaPlayer != null){
            try{
                mediaPlayer.stop();
            } catch (Exception e){
                e.printStackTrace();
            }
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try{
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    playPlayer();
                }
            });
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    flipPlayPauseButton(false);
                }
            });

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void pausePlayer(){
        try{
            mediaPlayer.pause();
            flipPlayPauseButton(false);

        }catch (Exception e){
            Log.d("EXCEPTION", "failed to pause mediaplayer");
        }
    }

    public void playPlayer(){
        try{
            //mediaPlayer.start();
            getAudioFocusAndPlay();
            flipPlayPauseButton(true);
           
        }catch (Exception e){
            Log.d("EXCEPTION", "failed to start mediaplayer");
        }
    }

    public void flipPlayPauseButton(boolean isPlaying){
        //code to communicate with main thread
        Intent intent = new Intent("changePlayButton");
        //add data
        intent.putExtra("isPlaying", isPlaying);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    public void togglePlayer(){
        Log.i("PLAYER", "ENTRO");
        try {
            if(mediaPlayer.isPlaying())
                pausePlayer();
            else
                playPlayer();
        }catch (Exception e){
            Log.d("EXCEPTION" , "failed to toggle mediaplayer");
        }
    }

    //Audio focus section
    private AudioManager am;
    public void getAudioFocusAndPlay(){
        am = (AudioManager) this.getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        //request audio focus
        int result = am.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC,  AudioManager.AUDIOFOCUS_GAIN);

        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            mediaPlayer.start();
        }
    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {

        }
    };
}
