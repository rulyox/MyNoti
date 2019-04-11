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

import androidx.core.app.NotificationCompat;

public class Service_Noti extends Service {

    // 알림
    Notification Noti;
    NotificationManager NotiMgr;
    String CHANNEL_ID = "Channel_Noti";

    SharedPreferences prefs;
    int aliveNumber;
    int notificationNumber;
    int noti_time_bool;

    int id;
    String title;
    String text;
    String color;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        prefs = getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
        aliveNumber = prefs.getInt("aliveNumber", 0);
        notificationNumber = prefs.getInt("notificationNumber", 1);
        noti_time_bool = prefs.getInt("notiTimeBoolean", 0);

        id = Integer.parseInt( intent.getStringExtra("id") );
        title = intent.getStringExtra("title");
        text = intent.getStringExtra("text");
        color = intent.getStringExtra("color");

        // 알림
        NotiMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(id < 0) NotiMgr.cancel(-1*id);
        else Notify(id,title,text, color);

        this.stopSelf();

        return startId;

    }

    // 알림 생성
    void Notify(int NotiID, String NotiTitle, String NotiText, String NotiColor){

        try {
            prefs = getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
            noti_time_bool = prefs.getInt("notiTimeBoolean", 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                Noti = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle(NotiTitle)
                        .setContentText(NotiText)
                        .setSmallIcon(R.drawable.icon_noti)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setShowWhen((noti_time_bool != 0))
                        .setColor(Color.parseColor(NotiColor))
                        .setGroup("" + NotiID)
                        .setChannelId(CHANNEL_ID)
                        .build();
                Noti.flags = Notification.FLAG_NO_CLEAR;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, getResources().getString(R.string.main_notifications), NotificationManager.IMPORTANCE_LOW);
                NotiMgr.createNotificationChannel(mChannel);
                NotiMgr.notify(NotiID, Noti);

            } else {

                Noti = new Notification.Builder(getApplicationContext())
                        .setContentTitle(NotiTitle)
                        .setContentText(NotiText)
                        .setSmallIcon(R.drawable.icon_noti)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setShowWhen((noti_time_bool != 0))
                        .setColor(Color.parseColor(NotiColor))
                        .setGroup("" + NotiID)
                        .build();
                Noti.flags = Notification.FLAG_NO_CLEAR;
                NotiMgr.notify(NotiID, Noti);

            }
        } catch (NullPointerException e){}

    }

}
