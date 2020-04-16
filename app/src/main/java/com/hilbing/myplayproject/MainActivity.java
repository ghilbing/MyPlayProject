package com.hilbing.myplayproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.hilbing.myplayproject.App.CHANNEL_1_ID;
import static com.hilbing.myplayproject.App.CHANNEL_2_ID;


public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPlaying = intent.getBooleanExtra("isPlaying", false);
            flipPlayPauseButton(isPlaying);
        }
    };
    static FloatingActionButton playPauseBtn;
    Button sendChannel1;
    Button sendChannel2;
    private NotificationManagerCompat notificationManagerCompat;
    private EditText titleET;
    private EditText messageET;
    PlayerService mBoundService;
    boolean mServiceBound = false;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlayerService.MyBinder myBinder = (PlayerService.MyBinder) iBinder;
            mBoundService = myBinder.getService();
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playPauseBtn = findViewById(R.id.fab);
        sendChannel1 = findViewById(R.id.send_channel1_BT);
        sendChannel2 = findViewById(R.id.send_channel2_BT);
        titleET = findViewById(R.id.title_ET);
        messageET = findViewById(R.id.message_ET);

        notificationManagerCompat = NotificationManagerCompat.from(this);


        final String url = "https://www.gretelhilbing.com//music_app/bensound-cute.mp3";


        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(mServiceBound)
                   mBoundService.togglePlayer();
            }
        });


        sendChannel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID);
                notification.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.bell_icon)
                        .setTicker("Channel 1")
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setCategory(Notification.CATEGORY_MESSAGE)
                        .setContentTitle(titleET.getText().toString())
                        .setContentText(messageET.getText().toString());

                notificationManagerCompat.notify(1, notification.build());

            }
        });

        sendChannel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_2_ID);
                notification.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setTicker("Channel 2")
                        .setPriority(Notification.PRIORITY_LOW)
                        .setContentTitle(titleET.getText().toString())
                        .setContentText(messageET.getText().toString());

                notificationManagerCompat.notify(2, notification.build());

            }
        });

        startStreamingService(url);

    }

    public void startStreamingService(String url){
        Intent intent = new Intent(this, PlayerService.class);
        intent.putExtra("url", "https://www.gretelhilbing.com//music_app/bensound-cute.mp3");
        intent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mServiceBound){
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    public static void flipPlayPauseButton(boolean isPlaying){
        if(isPlaying){
            playPauseBtn.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            playPauseBtn.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("changePlayButton"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

    }
}
