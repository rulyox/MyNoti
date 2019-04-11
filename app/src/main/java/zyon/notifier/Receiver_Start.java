package zyon.notifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class Receiver_Start extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent revive = new Intent(context, Service_Revive.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(revive);
        context.startService(revive);

    }

}