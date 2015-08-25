package learn2crack.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;

public class MSGReceiver  extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        Bundle extras = intent.getExtras();
        Intent msgrcv = new Intent("Msg");
        msgrcv.putExtra("msg_id", extras.getString("msg_id"));
        msgrcv.putExtra("from", extras.getString("fromu"));
        msgrcv.putExtra("type", extras.getString("type"));
        msgrcv.putExtra("tab", extras.getString("tab"));
        msgrcv.putExtra("status", extras.getString("status"));
        msgrcv.putExtra("selected_options", extras.getString("selected_options"));
        msgrcv.putExtra("associated_to_msg_id", extras.getString("associated_to_msg_id"));

        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
        ComponentName comp = new ComponentName(context.getPackageName(),MSGService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}