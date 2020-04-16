package com.hilbing.myplayproject;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;

import static android.app.Notification.PRIORITY_LOW;

public class PlayerService extends Service {

    MediaPlayer mediaPlayer = new MediaPlayer();

    private final IBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {
        PlayerService getService(){
            return PlayerService.this;
        }
    }

    public PlayerService() {
    }

  /*  @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(){
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            showNotification();
    }

    public Notification getNotification() {
        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel).setSmallIcon(android.R.drawable.ic_menu_mylocation).setContentTitle("snap map fake location");
        Notification notification = mBuilder
                .setPriority(PRIORITY_LOW)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();


        return notification;
    }

    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String name = "snap map fake location ";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel("snap map channel", name, importance);

        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }
        return "snap map channel";
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.hilbing.myplayproject";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        //notificationIntent.setFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent previousIntent = new Intent(this, PlayerService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent prevPendingIntent = PendingIntent.getActivity(this, 0, previousIntent, 0);

        Intent playIntent = new Intent(this, PlayerService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent playPendingIntent = PendingIntent.getActivity(this, 0, playIntent, 0);

        Intent nextIntent = new Intent(this, PlayerService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent nextPendingIntent = PendingIntent.getActivity(this, 0, nextIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.bell_icon);

        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getResources().getString(R.string.music_player))
                .setTicker(getResources().getString(R.string.playing_music))
                .setContentText(getResources().getString(R.string.my_song))
                .setSmallIcon(R.drawable.bell_icon)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_previous, "Previous", prevPendingIntent)
                .addAction(android.R.drawable.ic_media_play, "Play", playPendingIntent)
                .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent)
                .build();

        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

     *//*   NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.bell_icon)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification); *//*
    }
*/
  /*  @Override
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

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        //notificationIntent.setFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent previousIntent = new Intent(this, MainActivity.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent prevPendingIntent = PendingIntent.getActivity(this, 0, previousIntent, 0);

        Intent playIntent = new Intent(this, MainActivity.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent playPendingIntent = PendingIntent.getActivity(this, 0, playIntent, 0);

        Intent nextIntent = new Intent(this, MainActivity.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent nextPendingIntent = PendingIntent.getActivity(this, 0, nextIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.bell_icon);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getString(R.string.music_player))
                .setTicker(getResources().getString(R.string.playing_music))
                .setContentText(getResources().getString(R.string.my_song))
                .setSmallIcon(R.drawable.bell_icon)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_previous, "Previous", prevPendingIntent)
                .addAction(android.R.drawable.ic_media_play, "Play", playPendingIntent)
                .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent)
                .build();

        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);



    }*/

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
            mediaPlayer.start();
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
        try {
            if(mediaPlayer.isPlaying())
                pausePlayer();
            else
                playPlayer();
        }catch (Exception e){
            Log.d("EXCEPTION" , "failed to toglle mediaplayer");
        }
    }
}
