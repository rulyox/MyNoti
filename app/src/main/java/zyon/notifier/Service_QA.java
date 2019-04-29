package zyon.notifier;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class Service_QA extends Service {

    String CHANNEL_ID = "Channel_QA";
    String color;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences prefs = getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
        color = prefs.getString("quickaddColor", "#FF4081");

        int check = Integer.parseInt( intent.getStringExtra("check") );
        QuickAdd( check != 0 );

        this.stopSelf();

        return startId;

    }

    void QuickAdd(boolean checked){

        Notification Noti;
        NotificationManager NotiMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(checked){

            Intent QuickAddIntent = new Intent(this, Activity_Dialog_Add.class);
            QuickAddIntent.putExtra("qa", 1);
            PendingIntent Intent = PendingIntent.getActivity(this, 0, QuickAddIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                Noti =  new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle(getString(R.string.qa))
                        .setContentText(getString(R.string.qa_text))
                        .setSmallIcon(R.drawable.button_add)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setShowWhen(false)
                        .setContentIntent(Intent)
                        .setColor( Color.parseColor(color) )
                        .setGroup(""+0)
                        .setChannelId(CHANNEL_ID)
                        .build();
                Noti.flags = Notification.FLAG_NO_CLEAR;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, getResources().getString(R.string.qa), NotificationManager.IMPORTANCE_MIN);
                NotiMgr.createNotificationChannel(mChannel);
                NotiMgr.notify(0 , Noti);

            }else{

                Noti = new Notification.Builder(getApplicationContext())
                        .setContentTitle(getString(R.string.qa))
                        .setContentText(getString(R.string.qa_text))
                        .setSmallIcon(R.drawable.button_add)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setShowWhen(false)
                        .setPriority(Notification.PRIORITY_MIN)
                        .setContentIntent(Intent)
                        .setColor( Color.parseColor(color) )
                        .setGroup(""+0)
                        .build();
                Noti.flags = Notification.FLAG_NO_CLEAR;
                NotiMgr.notify(0, Noti);

            }

        } else { NotiMgr.cancel(0); }

    }

}
