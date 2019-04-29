package zyon.notifier;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class Service_Noti extends Service {

    // 알림
    Notification Noti;
    NotificationManager NotiMgr;
    String CHANNEL_ID = "Channel_Noti";

    SharedPreferences prefs;
    boolean show_time;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        prefs = getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
        show_time = prefs.getInt("notiTimeBoolean", 0) != 0;

        int id = Integer.parseInt( intent.getStringExtra("id") );
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        String color = intent.getStringExtra("color");

        // 알림
        NotiMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(id < 0) NotiMgr.cancel(-1*id);
        else Notify(id, title, text, color);

        this.stopSelf();

        return startId;

    }

    // 알림 생성
    void Notify(int id, String title, String text, String color){

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                Noti = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setSmallIcon(R.drawable.icon_noti)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setShowWhen(show_time)
                        .setColor(Color.parseColor(color))
                        .setGroup("" + id)
                        .setChannelId(CHANNEL_ID)
                        .build();
                Noti.flags = Notification.FLAG_NO_CLEAR;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, getResources().getString(R.string.main_notifications), NotificationManager.IMPORTANCE_LOW);
                NotiMgr.createNotificationChannel(mChannel);
                NotiMgr.notify(id, Noti);

            } else {

                Noti = new Notification.Builder(getApplicationContext())
                        .setContentTitle(title)
                        .setContentText(text)
                        .setSmallIcon(R.drawable.icon_noti)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setShowWhen(show_time)
                        .setColor(Color.parseColor(color))
                        .setGroup("" + id)
                        .build();
                Noti.flags = Notification.FLAG_NO_CLEAR;
                NotiMgr.notify(id, Noti);

            }

        } catch(NullPointerException e) { Log.d("Zyon", e.toString()); }

    }

}
